package com.square.streams.transport

import com.google.gson.Gson
import com.square.common.enums.{AppCode, EventMappingCode}
import com.square.common.utils.EdaRouteAdmin
import com.square.common.{Config, Constants}
import com.square.streams.{BinLog, CommonLog}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.elasticsearch.spark.rdd.EsSpark
import org.slf4j.LoggerFactory

object CanalSourceMain {
  val logger = LoggerFactory.getLogger(CanalSourceMain.getClass)

  var app_code = AppCode.REDBONUS.getValue
  var event_code = ""

  def main(args: Array[String]) {

    if (args.length < 2) {
      logger.error("输入的参数不正确")
      return
    }
    app_code = args(0).toString
    event_code = args(1).toString

    //设置打印日志级别
    System.setProperty("HADOOP_USER_NAME", "hdfs")
    Logger.getLogger("org.apache.kafka").setLevel(Level.ERROR)
    Logger.getLogger("org.apache.zookeeper").setLevel(Level.ERROR)
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    val Array(brokers, groupId, topics) = Array(Config.BROKER_LISTS, Constants.CONSUMER_GROUP_ID, event_code)

    val sparkConf = new SparkConf().setAppName("canal_" + app_code + "_" + event_code)
    //      .setMaster("local[2]")
    sparkConf.set("es.nodes", Config.ES_HOSTS)
    sparkConf.set("es.port", "9200")
    sparkConf.set("es.index.auto.create", "true")
    sparkConf.set("es.batch.size.entries", "1000")
    sparkConf.set("es.batch.flush.timeout", "60")
    sparkConf.set("spark.streaming.kafka.maxRatePerPartition", "10000")

    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.GROUP_ID_CONFIG -> groupId,
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (false: java.lang.Boolean), //设置自动提交为false,手动控制offset
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer])
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    startStream(ssc, kafkaParams, topicsSet)
    ssc.start()
    ssc.awaitTermination()
  }

  def startStream(ssc: StreamingContext, kafkaParams: Map[String, Object], topicsSet: scala.collection.immutable.Set[String]): Unit = {

    val inputStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      ssc, LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topicsSet, kafkaParams))

    val currentIndex = EdaRouteAdmin.getOrCreateIndex(app_code, event_code, EventMappingCode.getMapping(event_code))
    var currentIndexName = EdaRouteAdmin.updateRouteIndex(app_code, event_code,  EventMappingCode.getMapping(event_code), currentIndex, "create_time")

    //累加器，用作更新es索引
    var accum = 0;
    inputStream.foreachRDD(rddRaw => {
      val begin = System.currentTimeMillis
      if (accum % 60 == 0) {
        currentIndexName = EdaRouteAdmin.updateRouteIndex(app_code, event_code,  EventMappingCode.getMapping(event_code), currentIndex, "create_time")
      }

      val offsetRangesArray = rddRaw.asInstanceOf[HasOffsetRanges].offsetRanges
      for (offsetRange <- offsetRangesArray) {
        logger.warn("kafka offset :" + offsetRange)
      }

      val messagesRdd = rddRaw.filter(message => {
        //  过滤含有关键字的　日志
        if (message.value().contains(Constants.MYSQL_INSERT) || message.value().contains(Constants.MYSQL_UPDATE)) {
          //  json 解析比较耗费资源 ; 先用 str 过滤掉 大部分数据；然后再用　json　解析　过滤
          val json = new Gson()
          val data = json.fromJson(message.value(), classOf[BinLog])
          val eventCode = data.getTable()
          val sqlType = data.getType
          eventCode != null && eventCode.equals(event_code) && sqlType != null && (sqlType.equals(Constants.MYSQL_INSERT) || sqlType.equals(Constants.MYSQL_UPDATE))
        } else {
          false
        }

      }).map(message => {
        val json = new Gson()
        val data = json.fromJson(message.value(), classOf[BinLog])
        val owner_code = data.getDatabase

        val arr = data.getData
        var messages = ""

        val size = arr.size() - 1
        for (i <- 0 to size) {
          val dataStr = json.toJson(arr.get(i)).trim
          val dataOne = json.fromJson(dataStr, classOf[CommonLog])
          if (i == 0) {
            messages = setDataPer(dataStr, app_code, owner_code, event_code, dataOne.getId)
          } else if (i == arr.size()) {
            messages = messages + setDataPer(dataStr, app_code, owner_code, event_code, dataOne.getId)
          } else {
            messages = messages + "||" + setDataPer(dataStr, app_code, owner_code, event_code, dataOne.getId)
          }
        }
        messages
      }).flatMap(t => t.split("\\|\\|"))

      if (!messagesRdd.isEmpty()) {
        EsSpark.saveJsonToEs(messagesRdd, currentIndexName, Map("es.mapping.id" -> "unique_id"))
      }
      val end = System.currentTimeMillis
      logger.warn("time cost  : " + (end - begin) + " ms")

      //重置 kafka offset
      inputStream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRangesArray)
      accum += 1
    })
  }


  /**
    * 给数据设置属性
    *
    * @param data
    * @param appCode
    * @param ownerCode
    * @param eventCode
    * @param id
    * @return
    */
  def setDataPer(data: String, appCode: String, ownerCode: String, eventCode: String, id: String): String = {
    return data.substring(0, data.length - 1) + ",\"unique_id\": \"unique_id_value\",\"app_code\": \"app_code_value\",\"owner_code\": \"owner_code_value\",\"event_code\": \"event_code_value\"}"
      .replace("unique_id_value", ownerCode + "_" + id)
      .replace("app_code_value", appCode)
      .replace("owner_code_value", ownerCode)
      .replace("event_code_value", eventCode)
  }
}
