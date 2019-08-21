package com.square.jobs.data

import java.sql.Timestamp

import com.square.common.enums.EventMappingCode
import com.square.common.utils.{DateUtils, EsAdminUtil}
import org.apache.commons.lang3.StringUtils
import org.apache.log4j.Logger
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.elasticsearch.spark._

object SparkEsSkUserBaseinfo {


  val logger: Logger = Logger.getLogger(SparkEsSkUserBaseinfo.getClass)

  /**
    *
    * @param from
    * @param to
    * @return
    *
    */
  def readEsSkUserBaseinfoData(from: String, to: String, sparkSession: SparkSession): DataFrame = {
    //查询语句
    val queryStr: String =
      s"""
         			   |{
         			   |  "query": {
         			   |    "bool": {
         			   |      "must": [
         			   |        {
         			   |          "range": {
         			   |            "create_time": {
         			   |              "gte": "$from",
         			   |              "lt": "$to",
         			   |              "format": "yyyy-MM-dd HH:mm:ss"
         			   |            }
         			   |          }
         			   |        },
         			   |        {
         			   |          "term": {
         			   |            "app_code": "redbonus"
         			   |          }
         			   |        }
         			   |      ]
         			   |    }
         			   |  }
         			   |}
           """.stripMargin

    val index_s = EsAdminUtil.queryAllRouteIndexNames(EventMappingCode.SK_USER_BASEINFO.getValue, from, to)

    val schema = (new StructType).add("id", LongType)
      .add("user_id", LongType)
      .add("nick", StringType)
      .add("mobile", StringType)
      .add("invitecode", StringType)
      .add("robot_flag", LongType)
      .add("death_flag", LongType)
      .add("innernum_flag", LongType)
      .add("create_time", TimestampType)
      .add("unique_id", StringType)
      .add("app_code", StringType)
      .add("owner_code", StringType)
      .add("event_code", StringType)
      .add("is_real", BooleanType)
    var rdd_rz: RDD[Row] = null
    if (StringUtils.isEmpty(index_s)) {
      logger.info("未查询到{%s}类型索引:　".format(index_s))
      rdd_rz = sparkSession.sparkContext.parallelize(List(Row(-1l, -1l, "", "", "", -1l, -1l, -1l, new Timestamp(DateUtils.parseTime("0000-00-00 00:00:00").getTime), "", "", "", "", false)))
    } else {
      logger.info("查询的索引:　" + index_s)
      val rdd = sparkSession.sparkContext.esRDD(index_s, queryStr)
      rdd_rz = rdd.map(t => {
        val map = t._2
        val id = map.get("id").getOrElse("0").toString.toLong
        val user_id = map.get("user_id").getOrElse("0").toString.toLong
        val nick = map.get("nick").getOrElse("").toString
        val mobile = map.get("mobile").getOrElse("").toString
        val invitecode = map.get("invitecode").getOrElse("").toString
        val robot_flag = map.get("robot_flag").getOrElse("0").toString.toLong
        val death_flag = map.get("death_flag").getOrElse("0").toString.toLong
        val innernum_flag = map.get("innernum_flag").getOrElse("0").toString.toLong
        val create_time = new Timestamp(DateUtils.parseTime(map.get("create_time").getOrElse("0000-00-00 00:00:00").toString).getTime)
        val unique_id = map.get("unique_id").getOrElse("").toString
        val app_code = map.get("app_code").getOrElse("").toString
        val owner_code = map.get("owner_code").getOrElse("").toString
        val event_code = map.get("event_code").getOrElse("").toString
        var is_real = false;
        if (robot_flag == 0 && innernum_flag == 0 && death_flag == 0) {
          is_real = true
        }
        Row(id, user_id, nick, mobile, invitecode, robot_flag, death_flag, innernum_flag, create_time, unique_id, app_code, owner_code, event_code, is_real)
      })
    }
    val df: DataFrame = sparkSession.createDataFrame(rdd_rz, schema).persist(StorageLevel.MEMORY_AND_DISK)
    df.show()
    df.createOrReplaceGlobalTempView(EventMappingCode.SK_USER_BASEINFO.getValue)
    df
  }

}
