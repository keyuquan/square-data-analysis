package com.square.jobs.kpi

import com.square.common.enums.EventMappingCode
import com.square.common.utils.{DateUtils, EsAdminUtil}
import com.square.jobs.conf.DataJson
import com.square.jobs.data.{SparkEsSkBill, SparkEsSkUserBaseinfo}
import com.square.jobs.utils.{SparkEsUtils, SparkSessionUtils, StringUtils}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.slf4j.LoggerFactory

object DataValidation {
  var from: String = "";
  var to: String = ""
  val timeStr = " 00:00:00"
  val logger = LoggerFactory.getLogger(DataValidation.getClass)

  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      logger.error("输入的参数不正确")
      return
    }
    from = StringUtils.replaceBlank(args(0)) + timeStr
    to = StringUtils.replaceBlank(args(1)) + timeStr

    logger.warn(s" from : '$from'  , to '$to'")
    val sparkSession: SparkSession = SparkSessionUtils.getOrCreate("DataValidation")

    try {
      val df_sk_user: DataFrame = SparkEsSkUserBaseinfo.readEsSkUserBaseinfoData(from, to, sparkSession)
      val df_bill_data: DataFrame = SparkEsSkBill.readEsSkBillData(from, to, sparkSession)
      val df_sk_bonus: DataFrame = SparkEsUtils.readEsDataUtils(EventMappingCode.SK_REDBONUS, "create_time", from, to, DataJson.SK_REDBONUS, sparkSession)
      val df_sk_grab_bonus: DataFrame =  SparkEsUtils.readEsDataUtils(EventMappingCode.SK_REDBONUS_GRAB, "create_time", from, to, DataJson.SK_REDBONUS_GRAB, sparkSession)

      sparkSession.sql("select owner_code ,event_code,count(distinct id) data_count from  global_temp.sk_user_baseinfo  group  by app_code,owner_code,event_code order by  owner_code,event_code")
        .createOrReplaceGlobalTempView("user")

      sparkSession.sql("select owner_code ,event_code,count(distinct id) data_count from  global_temp.sk_bill  group  by app_code,owner_code,event_code order by  owner_code,event_code")
        .createOrReplaceGlobalTempView("bill")

      sparkSession.sql("select owner_code ,event_code,count(distinct id) data_count  from  global_temp.sk_redbonus  group  by app_code,owner_code ,event_code order by  owner_code,event_code")
        .createOrReplaceGlobalTempView("redbonus")

      sparkSession.sql("select owner_code ,event_code,count(distinct id) data_count from  global_temp.sk_redbonus_grab  group  by app_code,owner_code,event_code order by  owner_code,event_code")
        .createOrReplaceGlobalTempView("grab")

      sparkSession.sql(
        """
          |select *  from
          |(
          |select * from   global_temp.user
          |union all
          |select * from   global_temp.bill
          |union all
          |select * from   global_temp.redbonus
          |union all
          |select * from   global_temp.grab
          |) t order  by  owner_code,event_code
        """.stripMargin).show(100000)

      df_sk_user.unpersist()
      df_bill_data.unpersist()
      df_sk_bonus.unpersist()
      df_sk_grab_bonus.unpersist()

    } catch {
      case e: Exception => logger.error("execute error.", e)
    } finally {

      sparkSession.stop()
      EsAdminUtil.closeClient()
    }
  }
}
