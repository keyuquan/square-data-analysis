package com.square.jobs.kpi

import com.square.common.Constants
import com.square.common.enums.AppCode
import com.square.common.utils.{EdaRouteAdmin, EsAdminUtil, Utils}
import com.square.jobs.kpi.StDayMain.logger
import com.square.jobs.data.SparkEsSkUserBaseinfo
import com.square.jobs.utils.SparkSessionUtils
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.elasticsearch.spark.rdd.EsSpark

/**
  * 统计接入的业主
  */
object OwnerAllMain {

  def main(args: Array[String]): Unit = {
    val sparkSession: SparkSession = SparkSessionUtils.getOrCreate("OwnerAllMain")

    try {
      val df_user_baseinfo: DataFrame = SparkEsSkUserBaseinfo.readEsSkUserBaseinfoData("2017-01-01 00:00:00", "2117-01-01 00:00:00", sparkSession)

      val sql =
        """
          |select  app_code,owner_code,to_date(create_time) create_date  from
          | (
          |select app_code,owner_code,create_time, row_number() OVER (partition by  app_code,owner_code  order by create_time asc) rank from  global_temp.sk_user_baseinfo
          |where user_id >=0
          |) user where  rank =1
        """.stripMargin
      val df = sparkSession.sql(sql).persist(StorageLevel.MEMORY_AND_DISK)
      df.show()

      val rdd_rz = df.rdd.map(row => {
        val app_code = row.get(0).toString
        val owner_code = row.get(1).toString
        val create_date = row.get(2).toString + " 00:00:00"
        val id = Utils.hash(app_code + owner_code)
        Map("app_code" -> app_code, "owner_code" -> owner_code, "create_date" -> create_date, "id" -> id)
      })

      val index_user_pay = EdaRouteAdmin.getOrCreateIndex(AppCode.REDBONUS.getValue, Constants.OWNER_ALL_TYPE, Constants.OWNER_MAPPING)
      EsSpark.saveToEs(rdd_rz, index_user_pay.getIndexName + "/" + Constants.OWNER_ALL_TYPE, Map("es.mapping.id" -> "id"))
      EdaRouteAdmin.updateRouteIndex(AppCode.REDBONUS.getValue, Constants.OWNER_ALL_TYPE, Constants.OWNER_MAPPING, index_user_pay, "create_date")


      df.unpersist()
      df_user_baseinfo.unpersist()

    } catch {
      case e: Exception => logger.error("execute error.", e)
    } finally {

      sparkSession.stop()
      EsAdminUtil.closeClient()
    }
  }

}
