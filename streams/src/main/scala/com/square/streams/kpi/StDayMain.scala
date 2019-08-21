package com.square.streams.kpi

import com.alibaba.fastjson.{JSON, JSONException, JSONObject}
import com.square.common.enums.AppCode
import com.square.common.utils.EdaRouteAdmin
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

object StDayMain {
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
    val Array(brokers, groupId, topics) = Array(Config.BROKER_LISTS, Constants.CONSUMER_GROUP_ID, "sk_user_baseinfo,sk_finance_cash_draws")

    val sparkConf = new SparkConf().setAppName("flume_" + app_code + "_" + StDayMain)
    //      .setMaster("local[2]")
    sparkConf.set("es.nodes", Config.ES_HOSTS)
    sparkConf.set("es.port", "9200")
    sparkConf.set("es.index.auto.create", "true")
    sparkConf.set("es.batch.size.entries", "1000")
    sparkConf.set("es.batch.flush.timeout", "60")
    sparkConf.set("spark.streaming.kafka.maxRatePerPartition", "50000")

    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.GROUP_ID_CONFIG -> groupId,
      "auto.offset.reset" -> "earliest ",
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

    val currentIndex = EdaRouteAdmin.getOrCreateIndex(AppCode.REDBONUS.getValue, Constants.ST_DAY_TYPE, Constants.ST_DAY_MAPPING)
    var currentIndexName = EdaRouteAdmin.updateRouteIndex(AppCode.REDBONUS.getValue, Constants.ST_DAY_TYPE, Constants.ST_DAY_MAPPING, currentIndex, "create_date")

    //累加器，用作更新es索引
    var accum = 0;

    inputStream.foreachRDD(rddRaw => {
      val begin = System.currentTimeMillis
      if (accum % 60 == 0) {
        currentIndexName = EdaRouteAdmin.updateRouteIndex(AppCode.REDBONUS.getValue, Constants.ST_DAY_TYPE, Constants.ST_DAY_MAPPING, currentIndex, "create_date")
      }

      val offsetRangesArray = rddRaw.asInstanceOf[HasOffsetRanges].offsetRanges
      for (offsetRange <- offsetRangesArray) {
        logger.warn("kafka offset :" + offsetRange)
      }

      //  1 . union 所有数据 找出所有用户的信息
      rddRaw.map(item => JsonUtils.parseJson(item.value())).map(item => {
        val app_code = item.getString("app_code")
        val owner_code = item.getString("owner_code")
        val create_time = item.getInteger("create_time")
        val user_id = item.getInteger("user_id")
        ()
      })



      //重置 kafka offset
      inputStream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRangesArray)
      accum += 1
    })
  }


}
