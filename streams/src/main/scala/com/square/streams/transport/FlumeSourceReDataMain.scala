package com.square.streams.transport

import com.square.common.enums.{AppCode, EventMappingCode}
import com.square.common.utils.{EdaRouteAdmin, EsAdminUtil}
import com.square.common.{Config, Constants}
import com.square.streams.transport.FlumeSourceMain.{app_code, event_code, logger}
import com.square.streams.utils.JsonUtils
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.elasticsearch.spark.rdd.EsSpark
import org.slf4j.LoggerFactory

/**
  * SkBill  数据导入
  */
object FlumeSourceReDataMain {
  val logger = LoggerFactory.getLogger(FlumeSourceReDataMain.getClass)
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
    val Array(brokers, groupId, topics) = Array(Config.BROKER_LISTS, Constants.CONSUMER_RE_DATA_GROUP_ID, event_code + "_re_data")

    val sparkConf = new SparkConf().setAppName("flume_" + app_code + "_" + event_code + "_re_data")
      //.setMaster("local[2]")
    sparkConf.set("es.nodes", Config.ES_HOSTS)
    sparkConf.set("es.port", "9200")
    sparkConf.set("es.index.auto.create", "true")
    sparkConf.set("es.batch.size.entries", "1000")
    sparkConf.set("es.batch.flush.timeout", "60")
    sparkConf.set("spark.streaming.kafka.maxRatePerPartition", "200")

    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.GROUP_ID_CONFIG -> groupId,
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean), //设置自动提交为false,手动控制offset
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer])
    val ssc = new StreamingContext(sparkConf, Seconds(2))

    startStream(ssc, kafkaParams, topicsSet)
    ssc.start()
    ssc.awaitTermination()
  }

  def startStream(ssc: StreamingContext, kafkaParams: Map[String, Object], topicsSet: scala.collection.immutable.Set[String]): Unit = {


    val inputStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      ssc, LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topicsSet, kafkaParams))

    var currentIndex = EdaRouteAdmin.getOrCreateIndex(app_code, event_code, EventMappingCode.getMapping(event_code))
    var currentIndexName = EdaRouteAdmin.updateRouteIndex(app_code, event_code, EventMappingCode.getMapping(event_code), currentIndex, "create_time")

    var billRechargeIndex = EdaRouteAdmin.getOrCreateIndex(app_code, EventMappingCode.SK_BILL_RECHARGE.getValue, EventMappingCode.getMapping(EventMappingCode.SK_BILL_RECHARGE.getValue))
    var billRechargeName = EdaRouteAdmin.updateRouteIndex(app_code, EventMappingCode.SK_BILL_RECHARGE.getValue, EventMappingCode.getMapping(EventMappingCode.SK_BILL_RECHARGE.getValue), billRechargeIndex, "create_time")

    val app_code_broadcast = ssc.sparkContext.broadcast(app_code)
    val event_code_broadcast = ssc.sparkContext.broadcast(event_code)

    //累加器，用作更新es索引
    var accum = 0;
    EsAdminUtil.getRestHighLevelClient
    inputStream.foreachRDD(rddRaw => {
      val begin = System.currentTimeMillis
      if (accum % 60 == 0) {
        currentIndex = EdaRouteAdmin.getOrCreateIndex(app_code_broadcast.value, event_code_broadcast.value, EventMappingCode.getMapping(event_code_broadcast.value))
        currentIndexName = EdaRouteAdmin.updateRouteIndex(app_code_broadcast.value, event_code_broadcast.value, EventMappingCode.getMapping(event_code_broadcast.value), currentIndex, "create_time")

        if (event_code_broadcast.value.equals(EventMappingCode.SK_BILL.getValue)) {
          billRechargeIndex = EdaRouteAdmin.getOrCreateIndex(app_code_broadcast.value, EventMappingCode.SK_BILL_RECHARGE.getValue, EventMappingCode.getMapping(EventMappingCode.SK_BILL_RECHARGE.getValue))
          billRechargeName = EdaRouteAdmin.updateRouteIndex(app_code_broadcast.value, EventMappingCode.SK_BILL_RECHARGE.getValue, EventMappingCode.getMapping(event_code_broadcast.value), billRechargeIndex, "create_time")
        }

      }

      var dataCount = 0l
      val offsetRangesArray = rddRaw.asInstanceOf[HasOffsetRanges].offsetRanges
      for (offsetRange <- offsetRangesArray) {
        dataCount = dataCount + (offsetRange.untilOffset - offsetRange.fromOffset)
        logger.warn("kafka offset :" + offsetRange)
      }

      val messagesRdd = rddRaw.distinct().mapPartitions(it => {
        val rz = it.map(message => {
          val msg = message.value()
          val item = JsonUtils.parseJson(msg)
          val app_code = item.getString("app_code").trim
          val create_time = item.getString("create_time").trim
          val event_code_data = item.getString("event_code").trim
          val unique_id = item.getString("unique_id").trim

          val count = EsAdminUtil.getUniqueIdSize(app_code, event_code_data, create_time, unique_id)
          var existence = false
          if (event_code_data.equals(EventMappingCode.SK_USER_BASEINFO.getValue)) {
            //  user  允许更新
            existence = false
          } else if (count >= 1) {
            //  其他表不允许更新
            existence = true
          }
          var is_event_code = false
          if (event_code_data.equals(event_code_broadcast.value.trim)) {
            is_event_code = true
          }

          (msg, existence, is_event_code)
        })
        rz
      }).filter(t => {
        (!t._2) && t._3
      }).map(t => {
        t._1
      }).cache()

      if (!messagesRdd.isEmpty()) {
        EsSpark.saveJsonToEs(messagesRdd, currentIndexName, Map("es.mapping.id" -> "unique_id"))

        if (event_code_broadcast.value.equals(EventMappingCode.SK_BILL.getValue)) {
          //  支付数据
          val messagesRddRecharge = messagesRdd.filter(message => {
            val item = JsonUtils.parseJson(message)
            val billt_id = item.getString("billt_id").trim.toInt
            billt_id == 1 || billt_id == 2 || billt_id == 42
          })

          EsSpark.saveJsonToEs(messagesRddRecharge, billRechargeName, Map("es.mapping.id" -> "unique_id"))

        }
      }

      messagesRdd.unpersist()
      val end = System.currentTimeMillis
      logger.warn("dataCount :" + dataCount + " time cost  : " + (end - begin) + " ms")

      //重置 kafka offset
      inputStream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRangesArray)
      accum += 1
    })
  }
}
