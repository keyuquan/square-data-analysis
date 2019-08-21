package com.square.jobs.dao

import com.square.common.Constants
import com.square.common.enums.{AppCode, EventMappingCode}
import com.square.common.utils.{DateUtils, EdaRouteAdmin}
import com.square.jobs.conf.DataJson
import com.square.jobs.utils.SparkEsUtils
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.elasticsearch.spark.rdd.EsSpark

/**
  * 支付统计
  * type:
  * 充值方式1、线上，2、银行卡存款，3、后台充值
  *
  * billt_id:
  * 1	线上充值
  * 2	人工充值
  * 42	银行卡充值
  *
  *
  *
  */
object RechargeDao {

  /**
    *
    * @param from
    * @param to
    * @param sparkSession
    * @return
    */
  def getRechargeSt(from: String, to: String, sparkSession: SparkSession): DataFrame = {
    val df_recharge =SparkEsUtils.readEsDataUtils(EventMappingCode.SK_BILL_RECHARGE,"create_time", DateUtils.addDay(from, -100000), to, DataJson.SK_BILL_RECHARGE, sparkSession)
    val sql_details_day =
      s"""
         |select * from
         |(
         |select r.*,u.is_real,u.nick,u.mobile,u.invitecode, row_number() OVER (PARTITION BY r.app_code,r.owner_code,r.user_id  ORDER BY r.id asc) rank  from
         | global_temp.sk_bill_recharge  r
         |join global_temp.sk_user_baseinfo u on r.user_id= u.user_id  and r.app_code=u.app_code  and r.owner_code=u.owner_code
         | where r.billt_id in (1,2,42)
         |)st where to_date(st.create_time)>= to_date('$from')
      """.stripMargin
    val df_details_day = sparkSession.sql(sql_details_day).persist(StorageLevel.MEMORY_AND_DISK)
    df_details_day.show()
    df_details_day.createGlobalTempView("recharge")

    //  首充　二充　明细
    val sql_details =
      """
        |select md5(concat(app_code,owner_code,user_id,rank))  id ,app_code,owner_code, user_id,DATE_FORMAT(create_time,'yyyy-MM-dd HH:mm:ss')  create_date,rank,nick,mobile,invitecode,money  as amount,if(is_real,2,3) user_flag
        |from
        |global_temp.recharge where  rank=1  or   rank=2
      """.stripMargin
    val df_details = sparkSession.sql(sql_details).persist(StorageLevel.MEMORY_AND_DISK)
    val index_details = EdaRouteAdmin.getOrCreateOneIndex(AppCode.REDBONUS.getValue, Constants.RECHARGE_DETAILS_TYPE, Constants.RECHARGE_DETAILS_MAPPING)
    EsSpark.saveJsonToEs(df_details.toJSON.rdd, index_details.getIndexName + "/" + Constants.RECHARGE_DETAILS_TYPE, Map("es.mapping.id" -> "id"))
    EdaRouteAdmin.updateRouteIndex(AppCode.REDBONUS.getValue, Constants.RECHARGE_DETAILS_TYPE, Constants.RECHARGE_DETAILS_MAPPING, index_details, "create_date")

    val sql_st_day_pro =
      s"""
         |select   app_code,owner_code,to_date(create_time) create_date ,
         |count(distinct  user_id) recharge_user_count_1,
         |count(distinct   if(is_real,user_id,null)) recharge_user_count_2,
         |count(distinct   if(is_real,null,user_id)) recharge_user_count_3,
         |
         |count(distinct id) recharge_count_1,
         |count(distinct if(is_real,id,null)) recharge_count_2,
         |count(distinct if(is_real,null,id)) recharge_count_3,
         |
         |cast(sum(money) AS DECIMAL (32, 2)) recharge_money_1,
         |cast(sum(if(is_real,money,0)) AS DECIMAL (32, 2)) recharge_money_2,
         |cast(sum(if(is_real,0,money)) AS DECIMAL (32, 2)) recharge_money_3,
         |
         |count(distinct if(rank=1,id,null)) first_recharge_count_1,
         |count(distinct if(rank=1, if(is_real,id,null),null)) first_recharge_count_2,
         |count(distinct if(rank=1, if(is_real,null,id),null)) first_recharge_count_3,
         |
         |count(distinct if(rank =2,id,null)) second_recharge_count_1,
         |count(distinct if(rank =2,if(is_real,id,null),null)) second_recharge_count_2,
         |count(distinct if(rank =2,if(is_real,null,id),null)) second_recharge_count_3,
         |
         |count(distinct if(rank >=2,id,null)) renewal_recharge_count_1,
         |count(distinct if(rank >=2,if(is_real,id,null),null)) renewal_recharge_count_2,
         |count(distinct if(rank >=2 ,if(is_real,null,id),null)) renewal_recharge_count_3,
         |
         |cast(sum(if(rank=1,money,0)) AS DECIMAL (32, 2))  first_recharge_money_1,
         |cast(sum(if(rank=1,if(is_real,money,0),0)) AS DECIMAL (32, 2)) first_recharge_money_2,
         |cast(sum(if(rank=1,if(is_real,0,money),0)) AS DECIMAL (32, 2)) first_recharge_money_3,
         |
         |cast(sum(if( rank =2,money,0)) AS DECIMAL (32, 2)) second_recharge_money_1,
         |cast(sum(if(rank =2,if(is_real,money,0),0)) AS DECIMAL (32, 2)) second_recharge_money_2,
         |cast(sum(if(rank =2,if(is_real,0,money),0)) AS DECIMAL (32, 2)) second_recharge_money_3,
         |
         |cast(sum(if(rank>=2,money,0)) AS DECIMAL (32, 2)) renewal_recharge_money_1,
         |cast(sum(if(rank>=2,if(is_real,money,0),0)) AS DECIMAL (32, 2)) renewal_recharge_money_2,
         |cast(sum(if(rank>=2,if(is_real,0,money),0)) AS DECIMAL (32, 2)) renewal_recharge_money_3,
         |
         |count(distinct if(billt_id=1,user_id,null) ) recharge_user_count_1_1,
         |count(distinct if(billt_id=1,if(is_real,user_id,null),null) ) recharge_user_count_2_1,
         |count(distinct if(billt_id=1,if(is_real,null,user_id),null) ) recharge_user_count_3_1,
         |
         |count(distinct if(billt_id=1,id,null) ) recharge_count_1_1,
         |count(distinct if(billt_id=1,if(is_real,id,null),null) ) recharge_count_2_1,
         |count(distinct if(billt_id=1,if(is_real,null,id),null) ) recharge_count_3_1,
         |
         |cast(sum( if(billt_id=1,money,0)) AS DECIMAL (32, 2)) recharge_money_1_1,
         |cast(sum( if(billt_id=1,if(is_real,money,0),0)) AS DECIMAL (32, 2)) recharge_money_2_1,
         |cast(sum( if(billt_id=1,if(is_real,0,money),0)) AS DECIMAL (32, 2)) recharge_money_3_1,
         |
         |count(distinct if(billt_id=42,user_id,null) ) recharge_user_count_1_2,
         |count(distinct if(billt_id=42,if(is_real,user_id,null),null) ) recharge_user_count_2_2,
         |count(distinct if(billt_id=42,if(is_real,null,user_id),null) ) recharge_user_count_3_2,
         |
         |count(distinct if(billt_id=42,id,null) ) recharge_count_1_2,
         |count(distinct if(billt_id=42,if(is_real,id,null),null) ) recharge_count_2_2,
         |count(distinct if(billt_id=42,if(is_real,null,id),null) ) recharge_count_3_2,
         |
         |cast(sum( if(billt_id=42,money,0)) AS DECIMAL (32, 2)) recharge_money_1_2,
         |cast(sum( if(billt_id=42,if(is_real,money,0),0)) AS DECIMAL (32, 2)) recharge_money_2_2,
         |cast(sum( if(billt_id=42,if(is_real,0,money),0)) AS DECIMAL (32, 2)) recharge_money_3_2,
         |
         |count(distinct if(billt_id=2,user_id,null) ) recharge_user_count_1_3,
         |count(distinct if(billt_id=2,if(is_real,user_id,null),null) ) recharge_user_count_2_3,
         |count(distinct if(billt_id=2,if(is_real,null,user_id),null) ) recharge_user_count_3_3,
         |
         |count(distinct if(billt_id=2,id,null) ) recharge_count_1_3,
         |count(distinct if(billt_id=2,if(is_real,id,null),null) ) recharge_count_2_3,
         |count(distinct if(billt_id=2,if(is_real,null,id),null) ) recharge_count_3_3,
         |
         |cast(sum( if(billt_id=2,money,0)) AS DECIMAL (32, 2)) recharge_money_1_3,
         |cast(sum( if(billt_id=2,if(is_real,money,0),0)) AS DECIMAL (32, 2)) recharge_money_2_3,
         |cast(sum( if(billt_id=2,if(is_real,0,money),0)) AS DECIMAL (32, 2)) recharge_money_3_3
         |
         |from  global_temp.recharge
         |group  by   app_code,owner_code,to_date(create_time)
      """.stripMargin
    val df_st_day_pro = sparkSession.sql(sql_st_day_pro)
    df_st_day_pro.createOrReplaceGlobalTempView("st_day_recharge_pro")

    //  列转行 : 区分 真实玩家 和 内部号
    val sql_st_day =
      """
        |select app_code,owner_code,create_date,user_flag,recharge_user_count,recharge_count,recharge_money,first_recharge_count,second_recharge_count,renewal_recharge_count, first_recharge_money,second_recharge_money,renewal_recharge_money,recharge_user_count_1,recharge_count_1,recharge_money_1,recharge_user_count_2,recharge_count_2,recharge_money_2,recharge_user_count_3,recharge_count_3,recharge_money_3 from
        |(
        |     select   app_code,owner_code, create_date , 1 user_flag,recharge_user_count_1 recharge_user_count,recharge_count_1 recharge_count,recharge_money_1 recharge_money,first_recharge_count_1 first_recharge_count,second_recharge_count_1 second_recharge_count,renewal_recharge_count_1 renewal_recharge_count,first_recharge_money_1 first_recharge_money,second_recharge_money_1 second_recharge_money,renewal_recharge_money_1 renewal_recharge_money,
        |     recharge_user_count_1_1 recharge_user_count_1,recharge_count_1_1 recharge_count_1,recharge_money_1_1 recharge_money_1,
        |     recharge_user_count_1_2 recharge_user_count_2,recharge_count_1_2 recharge_count_2,recharge_money_1_2 recharge_money_2,
        |     recharge_user_count_1_3 recharge_user_count_3,recharge_count_1_3 recharge_count_3,recharge_money_1_3  recharge_money_3 from   global_temp.st_day_recharge_pro
        |    union
        |     select   app_code,owner_code, create_date , 2 user_flag,recharge_user_count_2 recharge_user_count,recharge_count_2 recharge_count,recharge_money_2 recharge_money,first_recharge_count_2 first_recharge_count,second_recharge_count_2 second_recharge_count,renewal_recharge_count_2 renewal_recharge_count,first_recharge_money_2 first_recharge_money,second_recharge_money_2 second_recharge_money,renewal_recharge_money_2 renewal_recharge_money,
        |     recharge_user_count_2_1 recharge_user_count_1,recharge_count_2_1 recharge_count_1,recharge_money_2_1 recharge_money_1,
        |     recharge_user_count_2_2 recharge_user_count_2,recharge_count_2_2 recharge_count_2,recharge_money_2_2 recharge_money_2,
        |     recharge_user_count_2_3 recharge_user_count_3,recharge_count_2_3 recharge_count_3,recharge_money_2_3  recharge_money_3 from   global_temp.st_day_recharge_pro
        |    union
        |     select   app_code,owner_code,create_date , 3 user_flag,recharge_user_count_3 recharge_user_count,recharge_count_3 recharge_count,recharge_money_3 recharge_money,first_recharge_count_3 first_recharge_count,second_recharge_count_3 second_recharge_count,renewal_recharge_count_3 renewal_recharge_count,first_recharge_money_3 first_recharge_money,second_recharge_money_3 second_recharge_money,renewal_recharge_money_3 renewal_recharge_money,
        |     recharge_user_count_3_1 recharge_user_count_1,recharge_count_3_1 recharge_count_1,recharge_money_3_1 recharge_money_1,
        |     recharge_user_count_3_2 recharge_user_count_2,recharge_count_3_2 recharge_count_2,recharge_money_3_2 recharge_money_2,
        |     recharge_user_count_3_3 recharge_user_count_3,recharge_count_3_3 recharge_count_3,recharge_money_3_3  recharge_money_3 from   global_temp.st_day_recharge_pro
        | ) st
      """.stripMargin
    val df_st_day = sparkSession.sql(sql_st_day)
    df_details.unpersist()
    df_details_day.unpersist()
    df_recharge.unpersist()
    return df_st_day
  }
}
