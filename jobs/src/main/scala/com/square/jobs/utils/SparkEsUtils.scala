package com.square.jobs.utils

import com.square.common.enums.EventMappingCode
import com.square.common.utils.EsAdminUtil
import org.apache.log4j.Logger
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.elasticsearch.spark._

object SparkEsUtils {

  val logger: Logger = Logger.getLogger(SparkEsUtils.getClass)

  /**
    *
    * @param eventCode 读取的数据类型
    * @param from      开始时间
    * @param to        结束时间
    * @param josn      没有数据的默认数据
    * @param sparkSession
    * @return
    */
  def readEsDataUtils(eventCode: EventMappingCode, param: String, from: String, to: String, josn: String, sparkSession: SparkSession): DataFrame = {
    //查询语句
    val queryStr: String =
      s"""
         			   |{
         			   |  "query": {
         			   |    "bool": {
         			   |      "must": [
         			   |        {
         			   |          "range": {
         			   |            "$param": {
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

    val index_s = EsAdminUtil.queryAllRouteIndexNames(eventCode.getValue, from, to)
    import sparkSession.implicits._

    if (org.apache.commons.lang3.StringUtils.isEmpty(index_s)) {
      logger.info("未查询到{%s}类型索引:　".format(index_s))
      val list = List(josn)
      val rdd = sparkSession.sparkContext.parallelize(list)
      val df = sparkSession.read.json(rdd.toDS).persist(StorageLevel.MEMORY_AND_DISK)
      df.show()
      df.createOrReplaceGlobalTempView(eventCode.getValue)
      df
    } else {
      logger.info("查询的索引:　" + index_s)
      val rdd = sparkSession.sparkContext.esJsonRDD(index_s, queryStr).map(t => {
        t._2
      })

      val df = sparkSession.read.json(rdd.toDS).persist(StorageLevel.MEMORY_AND_DISK)
      df.show()
      df.createOrReplaceGlobalTempView(eventCode.getValue)
      df

    }

  }

}
