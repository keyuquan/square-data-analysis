package com.square.jobs.utils

import com.square.common.enums.EventMappingCode
import com.square.common.utils.DateUtils
import com.square.jobs.conf.DataJson
import com.square.jobs.kpi.StDayMain.{from, to}
import org.apache.spark.sql.types.{IntegerType, StringType, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.storage.StorageLevel

import scala.collection.mutable.ListBuffer

/**
  * 创建全表的工具
  */
object SchemaUtils {

  /**
    * 根据时间 创建全表数据
    *
    * @param from
    * @param to
    * @param sparkSession
    */
  def CreateSchemaTable(from: String, to: String, sparkSession: SparkSession): DataFrame = {

    //  1.读取业主全表　　owner_all　
    val df_owner =SparkEsUtils.readEsDataUtils(EventMappingCode.OWNER_ALL,"create_date", DateUtils.addDay(from, -100000), to, DataJson.OWNER_ALL, sparkSession)

    // 2. 根据时间起至 创建时间全表
    val days = DateUtils.differentDays(from,to,  DateUtils.DATE_FULL_FORMAT)
    System.out.println("days", days)
    val list_date = ListBuffer[Row]();
    for (day <- 0.to(days-1)) {
      for (userFlag <- 1.to(3)) {
        val row = Row(DateUtils.addDay(from, day), userFlag)
        list_date.append(row)
      }
    }
    val schema_date = (new StructType)
      .add("create_date", StringType)
      .add("user_flag", IntegerType)

    val rdd_rz = sparkSession.sparkContext.parallelize(list_date)
    val df_date = sparkSession.createDataFrame(rdd_rz, schema_date).persist(StorageLevel.MEMORY_AND_DISK)
    df_date.show()
    df_date.createOrReplaceGlobalTempView("date_all")

    //  3. 2个表join  构建全表
    val sql =
      """
        |select o.app_code,o.owner_code, to_date(d.create_date) create_date,d.user_flag from
        |global_temp.owner_all  o
        |join global_temp.date_all  d  on  to_date(o.create_date) <= to_date(d.create_date)
      """.stripMargin
    val df = sparkSession.sql(sql)
    df_date.unpersist()
    df_owner.unpersist()
    df
  }
}
