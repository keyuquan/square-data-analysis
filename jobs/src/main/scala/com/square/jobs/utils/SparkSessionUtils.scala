package com.square.jobs.utils

import com.square.common.Config
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

/**
  * Created by kequan on 3/27/18.
  */
object SparkSessionUtils {

  /**
    * 初始化 SparkSession
    */
  def getOrCreate(name: String): SparkSession = {

    Logger.getLogger("org.apache.kafka").setLevel(Level.ERROR)
    Logger.getLogger("org.apache.zookeeper").setLevel(Level.ERROR)
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.spark_project").setLevel(Level.ERROR)
    Logger.getLogger("org.elasticsearch").setLevel(Level.ERROR)

    SparkSession
      .builder()
      .appName(name)
      .config("spark.default.parallelism", "20")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .config("spark.shuffle.consolidateFiles", "true")
      .config("spark.sql.shuffle.partitions", "10")
      .config("spark.ui.retainedJobs", "50")
      .config("spark.ui.retainedStages", "100")
      .config("spark.port.maxRetries", "100") // 最多绑定 端口次数
      .config("spark.debug.maxToStringFields", "500") // spark 表 最多字段数
      //.master("local[2]")
      // es 设置
      .config("es.nodes", Config.ES_HOSTS)
      .config("es.port", "9200")
      .config("es.index.auto.create", "true")
      .config("es.index.read.missing.as.empty", "true")
      .config("es.batch.size.entries", "1000")
      .config("es.batch.flush.timeout", "60")
      .config("es.mapping.date.rich", "false")
      .getOrCreate()

  }

}
