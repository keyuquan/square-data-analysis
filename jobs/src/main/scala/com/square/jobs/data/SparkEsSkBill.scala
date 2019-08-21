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

object SparkEsSkBill {

  val logger: Logger = Logger.getLogger(SparkEsSkBill.getClass)

  /**
    *
    * @param from
    * @param to
    * @return
    *
    */
  def readEsSkBillData(from: String, to: String, sparkSession: SparkSession): DataFrame = {
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

    val index_s = EsAdminUtil.queryAllRouteIndexNames(EventMappingCode.SK_BILL.getValue, from, to)

    val schema = (new StructType).add("id", LongType)
      .add("user_id", LongType)
      .add("billt_id", LongType)
      .add("money", DoubleType)
      .add("create_time", TimestampType)
      .add("biz_id", LongType)
      .add("unique_id", StringType)
      .add("app_code", StringType)
      .add("owner_code", StringType)
      .add("event_code", StringType)
      .add("is_active", BooleanType)
      .add("is_reward", BooleanType)
      .add("is_commision", BooleanType)
    var rdd_rz: RDD[Row] = null
    if (StringUtils.isEmpty(index_s)) {
      logger.info("未查询到{%s}类型索引:　".format(index_s))
      rdd_rz = sparkSession.sparkContext.parallelize(List(Row(-1l, -1l, -1l, 0d, new Timestamp(DateUtils.parseTime("0000-00-00 00:00:00").getTime), -1l, "", "", "", "", false, false, false)))
    } else {
      logger.info("查询的索引:　" + index_s)
      val rdd = sparkSession.sparkContext.esRDD(index_s, queryStr)
      rdd_rz = rdd.map(t => {
        val map = t._2
        val id = map.get("id").getOrElse("0").toString.toLong
        val user_id = map.get("user_id").getOrElse("0").toString.toLong
        val billt_id = map.get("billt_id").getOrElse("0").toString.toLong
        var money = map.get("money").getOrElse("0").toString.toDouble

        if (billt_id == 14) {
          val intro = map.get("intro").getOrElse("0").toString
          money = 0 -  intro.replace(",", "").replace("提现成功","").replace("元","").toDouble
        }

        if (billt_id == 11 || billt_id == 15) {
          money = 0d
        }

        val create_time = new Timestamp(DateUtils.parseTime(map.get("create_time").getOrElse("0000-00-00 00:00:00").toString).getTime)
        val biz_id = map.get("biz_id").getOrElse("0").toString.toLong
        val unique_id = map.get("unique_id").getOrElse("").toString
        val app_code = map.get("app_code").getOrElse("").toString
        val owner_code = map.get("owner_code").getOrElse("").toString
        val event_code = map.get("event_code").getOrElse("").toString
        // 是否活跃
        var is_active = false

        if (billt_id == 3 || billt_id == 5 || billt_id == 24 || billt_id == 25 || billt_id == 32 || billt_id == 36 || billt_id == 41 || billt_id == 46) {
          is_active = true;
          // 牛牛: 24 ,25
          // 扫雷: 3,5
          // 禁抢: 41,46
          //  32  幸运大转盘
          //  36  水果机投注
        }

        //  是否是福利人数 19  20 27  28  30  21 22 22 16  43   8 9  31  38
        var is_reward = false
        //  是否是佣金
        var is_commision = false

        if ((billt_id == 19) || (billt_id == 20) || (billt_id == 27) || (billt_id == 28) || (billt_id == 30) || (billt_id == 21) || (billt_id == 22) || (billt_id == 23) || (billt_id == 16) || (billt_id == 43) || (billt_id == 8) || (billt_id == 9) || (billt_id == 31) || (billt_id == 38)) {
          is_reward = true

          if ((billt_id == 8) || (billt_id == 9) || (billt_id == 31) || (billt_id == 38)) {
            is_commision = true
          }
        }

        Row(id, user_id, billt_id, money, create_time, biz_id, unique_id, app_code, owner_code, event_code, is_active, is_reward, is_commision)
      })
    }
    val df: DataFrame = sparkSession.createDataFrame(rdd_rz, schema).persist(StorageLevel.MEMORY_AND_DISK)
    df.show()
    df.createOrReplaceGlobalTempView(EventMappingCode.SK_BILL.getValue)
    df
  }


  /**
    *
    * 全量 读取  bill  的支付信息
    *
    * @return
    *
    */
  def readEsSkBillAllRechargeData(from: String, to: String, sparkSession: SparkSession, isCache: Boolean): DataFrame = {
    //查询语句
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

    val index_s = EsAdminUtil.queryAllRouteIndexNames(EventMappingCode.SK_BILL_RECHARGE.getValue, from, to)

    val schema = (new StructType).add("id", LongType)
      .add("user_id", LongType)
      .add("billt_id", LongType)
      .add("money", DoubleType)
      .add("create_time", TimestampType)
      .add("biz_id", LongType)
      .add("unique_id", StringType)
      .add("app_code", StringType)
      .add("owner_code", StringType)
      .add("event_code", StringType)
    var rdd_rz: RDD[Row] = null
    if (StringUtils.isEmpty(index_s)) {
      logger.info("未查询到{%s}类型索引:　".format(index_s))
      rdd_rz = sparkSession.sparkContext.parallelize(List(Row(-1l, -1l, -1l, 0d, new Timestamp(DateUtils.parseTime("0000-00-00 00:00:00").getTime), -1l, "", "", "", "")))
    } else {
      logger.info("查询的索引:　" + index_s)
      val rdd = sparkSession.sparkContext.esRDD(index_s, queryStr)
      rdd_rz = rdd.map(t => {
        val map = t._2
        val id = map.get("id").getOrElse("0").toString.toLong
        val user_id = map.get("user_id").getOrElse("0").toString.toLong
        val billt_id = map.get("billt_id").getOrElse("0").toString.toLong
        val money = map.get("money").getOrElse("0").toString.toDouble
        val create_time = new Timestamp(DateUtils.parseTime(map.get("create_time").getOrElse("0000-00-00 00:00:00").toString).getTime)
        val biz_id = map.get("biz_id").getOrElse("0").toString.toLong
        val unique_id = map.get("unique_id").getOrElse("").toString
        val app_code = map.get("app_code").getOrElse("").toString
        val owner_code = map.get("owner_code").getOrElse("").toString
        val event_code = map.get("event_code").getOrElse("").toString


        Row(id, user_id, billt_id, money, create_time, biz_id, unique_id, app_code, owner_code, event_code)
      })
    }
    if (isCache) {
      val df: DataFrame = sparkSession.createDataFrame(rdd_rz, schema).persist(StorageLevel.MEMORY_AND_DISK)
      df.show()
      df.createOrReplaceGlobalTempView(EventMappingCode.SK_BILL_RECHARGE.getValue)
      df
    } else {
      val df: DataFrame = sparkSession.createDataFrame(rdd_rz, schema)
      df.createOrReplaceGlobalTempView(EventMappingCode.SK_BILL_RECHARGE.getValue)
      df
    }
  }


}
