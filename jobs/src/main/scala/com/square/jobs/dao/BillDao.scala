package com.square.jobs.dao

import com.square.jobs.data.SparkEsSkBill
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel

/**
  * 统计用户活跃数
  *
  * 牛牛: 24 ,25
  * 扫雷: 3,5
  * 禁抢: 41,46
  * 水果游戏: 36
  * 幸运大转盘:  32
  *
  */
object BillDao {

  def getBillDetails(from: String, to: String, sparkSession: SparkSession): DataFrame = {
    val df_bill_data: DataFrame = SparkEsSkBill.readEsSkBillData(from, to, sparkSession)
    // 全部 ：  24,25,40,3,4,5,6,18, 34,41,46, 44,45,19,20,27,28,30,21,22,23,16,43,8,9,31,38, 36,37,32,43,14,11,15, 1,2,42，7
    // 牛牛： 24,25,40,
    // 扫雷： 3,4,5,6,18,
    // 禁抢： 34,41,46,
    // 福利发包 ： 44,45,
    //  奖励： 19,20,27,28,30,21,22,23,16,43,8,9,31,38,
    //  水果机:  36,37,
    //  幸运大转盘： 32,
    //  救援金 43,
    //  提现： 14,11,15,
    //  充值： 1,2,42
    // 系统 调整： 7
    val sql =
    """
      |select distinct bill.*,u.is_real,u.death_flag
      |  from
      |  global_temp.sk_bill   bill
      |  join  global_temp.sk_user_baseinfo u on bill.user_id= u.user_id and bill.app_code=u.app_code  and bill.owner_code=u.owner_code
      |  where  u.user_id >= 0
    """.stripMargin

    val df = sparkSession.sql(sql)

    df_bill_data.unpersist()
    df
  }

  /**
    * 活跃用户数  和 会员盈利 和 福利人数
    *
    * @param from
    * @param to
    * @param sparkSession
    * @return
    */
  def getBillStUser(from: String, to: String, sparkSession: SparkSession): DataFrame = {

    val sql_st_day_pro =
      """
        |select  app_code,owner_code,to_date(create_time)  create_date ,
        |count(distinct if(is_active,user_id,null)) active_user_count_1 ,
        |count(distinct if(is_real,if(is_active,user_id,null),null)) active_user_count_2 ,
        |count(distinct if(is_real,null,if(is_active,user_id,null))) active_user_count_3 ,
        |count(distinct if(is_reward,user_id,null)) reward_user_count_1 ,
        |count(distinct if(is_real,if(is_reward,user_id,null),null)) reward_user_count_2 ,
        |count(distinct if(is_real,null,if(is_reward,user_id,null))) reward_user_count_3 ,
        |count(distinct if(is_commision,user_id,null)) commision_user_count_1 ,
        |count(distinct if(is_real,if(is_commision,user_id,null),null)) commision_user_count_2 ,
        |count(distinct if(is_real,null,if(is_commision,user_id,null))) commision_user_count_3 ,
        |cast(sum(money) AS DECIMAL (32, 2))   user_profit_1 ,
        |cast(sum(if(is_real,money,0)) AS DECIMAL (32, 2))   user_profit_2 ,
        |cast(sum(if(is_real,0,money)) AS DECIMAL (32, 2))  user_profit_3
        |from
        |   global_temp.bill_details
        |group  by   app_code,owner_code,to_date(create_time)
      """.stripMargin
    val df_st_day_pro = sparkSession.sql(sql_st_day_pro).persist(StorageLevel.MEMORY_AND_DISK)
    df_st_day_pro.show()
    df_st_day_pro.createOrReplaceGlobalTempView("st_day_bill_pro")

    //  列转行 : 区分 真实玩家 和 内部号
    val sql_st_day =
      """
        |select  s.app_code, s.owner_code, s.create_date, s.user_flag,IFNULL(active_user_count, 0) active_user_count,IFNULL(reward_user_count, 0)  reward_user_count,IFNULL(commision_user_count, 0)  commision_user_count,IFNULL(user_profit, 0) user_profit from
        |global_temp.st_schema s
        |left join
        |(
        |    select app_code,owner_code, create_date,1 user_flag ,active_user_count_1 active_user_count, reward_user_count_1 reward_user_count,commision_user_count_1 commision_user_count,user_profit_1 user_profit from  global_temp.st_day_bill_pro
        |    union
        |    select app_code,owner_code, create_date,2 user_flag ,active_user_count_2 active_user_count, reward_user_count_2 reward_user_count,commision_user_count_2 commision_user_count,user_profit_2 user_profit from  global_temp.st_day_bill_pro
        |    union
        |    select app_code,owner_code, create_date,3 user_flag ,active_user_count_3 active_user_count, reward_user_count_3 reward_user_count,commision_user_count_3 commision_user_count,user_profit_3 user_profit from  global_temp.st_day_bill_pro
        |) st on  s.app_code=st.app_code and   s.owner_code=st.owner_code  and  s.create_date=st.create_date  and  s.user_flag=st.user_flag
      """.stripMargin
    val df_st_day = sparkSession.sql(sql_st_day).persist(StorageLevel.MEMORY_AND_DISK)
    df_st_day.show()
    df_st_day.createOrReplaceGlobalTempView("st_day_bill")

    // 根据历史数据 统计累计  user_profit 即是  initial_balance 、 final_balance
    val sql_st_all_pro =
      s"""
         |select   app_code,owner_code,create_date,user_flag,active_user_count,user_profit,reward_user_count,commision_user_count,final_balance,rank from
         |(
         |    select app_code,owner_code,create_date,user_flag,active_user_count,reward_user_count,commision_user_count,user_profit ,cast(sum(user_profit) over (partition by  app_code,owner_code,user_flag  order by create_date asc) AS DECIMAL (32, 2))  final_balance,row_number() OVER (partition by  app_code,owner_code,user_flag  order by create_date asc) rank from
         |    (
         |    select app_code,owner_code,to_date(create_date) create_date,user_flag,active_user_count,reward_user_count,commision_user_count,user_profit from  global_temp.st_day_bill
         |    union
         |    select app_code,owner_code,to_date(create_date) create_date,user_flag,active_user_count,reward_user_count,commision_user_count,user_profit from  global_temp.st_day
         |    ) st
         | ) st2
      """.stripMargin
    val df_st_all_pro = sparkSession.sql(sql_st_all_pro).persist(StorageLevel.MEMORY_AND_DISK)
    df_st_all_pro.show()
    df_st_all_pro.createOrReplaceGlobalTempView("st_bill_all_pro")

    //  join 出 initial_balance
    val sql_st_all =
      s"""
         |select  b1.app_code,b1.owner_code,b1.create_date,b1.user_flag,b1.active_user_count,b1.reward_user_count,b1.commision_user_count,cast(b1.user_profit  AS DECIMAL (32, 2)) user_profit ,cast(b1.final_balance AS DECIMAL (32, 2)) final_balance, cast( IFNULL(b2.final_balance,0)  AS DECIMAL (32, 2)) initial_balance from
         |  global_temp.st_bill_all_pro b1
         |  left join  global_temp.st_bill_all_pro b2  on  b1.app_code=b2.app_code and   b1.owner_code=b2.owner_code and  b1.user_flag=b2.user_flag
         |where b1.rank=b2.rank+1 and  b1.create_date>= to_date('$from')
      """.stripMargin
    val df_st_all = sparkSession.sql(sql_st_all)

    df_st_day.unpersist()
    df_st_day_pro.unpersist()
    df_st_all_pro.unpersist()
    return df_st_all
  }

  /**
    * 获取账单统计
    *
    * @param from
    * @param to
    * @param sparkSession
    * @return
    */
  def getBillSt(from: String, to: String, sparkSession: SparkSession): DataFrame = {

    //  全部　
    val sql_bill_type =
      """
        |SELECT app_code ,owner_code ,to_date (create_time)  create_date,billt_id,
        |cast(sum(money) AS DECIMAL (32,2))  sumall_1,
        |cast(sum(if(is_real,money,0)) AS DECIMAL (32,2))  sumall_2,
        |cast(sum(if(is_real,0,money)) AS DECIMAL (32,2))  sumall_3,
        |count(distinct user_id)  count_1,
        |count(distinct if(is_real,user_id,null) )  count_2,
        |count(distinct if(is_real,null,user_id))  count_3,
        |count(distinct id )  count_id_1 ,
        |count(distinct if(is_real,id,null) )  count_id_2 ,
        |count(distinct if(is_real,null,id) )  count_id_3
        |FROM
        |global_temp.bill_details
        |WHERE billt_id IN (40,36, 37, 19, 20, 21, 22, 23, 16, 27, 28, 30, 43, 32, 44, 45, 8, 31, 9, 38, 14,11,15)  GROUP BY to_date (create_time),app_code,owner_code,billt_id
      """.stripMargin
    val df_bill_type = sparkSession.sql(sql_bill_type)

    val nums: List[Long] = List(40, 36, 37, 19, 20, 21, 22, 23, 16, 27, 28, 30, 43, 32, 44, 45, 8, 31, 9, 38, 7, 14, 11, 15)
    val df_rz_bill_type: DataFrame = df_bill_type.groupBy("app_code", "owner_code", "create_date").pivot("billt_id", nums)
      .max("sumall_1", "count_1", "count_id_1", "sumall_2", "count_2", "count_id_2", "sumall_3", "count_3", "count_id_3").persist(StorageLevel.MEMORY_AND_DISK)
    df_rz_bill_type.createOrReplaceGlobalTempView("st_bill_type")

    val sql_union =
      """
        |select
        | app_code,owner_code,create_date , 1 user_flag ,
        |`40_max(sumall_1)` sumall_40,`40_max(count_1)` count_40,`40_max(count_id_1)` count_id_40,
        |`36_max(sumall_1)` sumall_36,`36_max(count_1)` count_36,`36_max(count_id_1)` count_id_36,
        |`37_max(sumall_1)` sumall_37,`37_max(count_1)` count_37,`37_max(count_id_1)` count_id_37,
        |`19_max(sumall_1)` sumall_19,`19_max(count_1)` count_19,`19_max(count_id_1)` count_id_19,
        |`20_max(sumall_1)` sumall_20,`20_max(count_1)` count_20,`20_max(count_id_1)` count_id_20,
        |`21_max(sumall_1)` sumall_21,`21_max(count_1)` count_21,`21_max(count_id_1)` count_id_21,
        |`22_max(sumall_1)` sumall_22,`22_max(count_1)` count_22,`22_max(count_id_1)` count_id_22,
        |`23_max(sumall_1)` sumall_23,`23_max(count_1)` count_23,`23_max(count_id_1)` count_id_23,
        |`16_max(sumall_1)` sumall_16,`16_max(count_1)` count_16,`16_max(count_id_1)` count_id_16,
        |`27_max(sumall_1)` sumall_27,`27_max(count_1)` count_27,`27_max(count_id_1)` count_id_27,
        |`28_max(sumall_1)` sumall_28,`28_max(count_1)` count_28,`28_max(count_id_1)` count_id_28,
        |`30_max(sumall_1)` sumall_30,`30_max(count_1)` count_30,`30_max(count_id_1)` count_id_30,
        |`43_max(sumall_1)` sumall_43,`43_max(count_1)` count_43,`43_max(count_id_1)` count_id_43,
        |`32_max(sumall_1)` sumall_32,`32_max(count_1)` count_32,`32_max(count_id_1)` count_id_32,
        |`44_max(sumall_1)` sumall_44,`44_max(count_1)` count_44,`44_max(count_id_1)` count_id_44,
        |`45_max(sumall_1)` sumall_45,`45_max(count_1)` count_45,`45_max(count_id_1)` count_id_45,
        |`8_max(sumall_1)` sumall_8,`8_max(count_1)` count_8,`8_max(count_id_1)` count_id_8,
        |`31_max(sumall_1)` sumall_31,`31_max(count_1)` count_31,`31_max(count_id_1)` count_id_31,
        |`9_max(sumall_1)` sumall_9,`9_max(count_1)` count_9,`9_max(count_id_1)` count_id_9,
        |`38_max(sumall_1)` sumall_38,`38_max(count_1)` count_38,`38_max(count_id_1)` count_id_38,
        |`7_max(sumall_1)` sumall_7,`7_max(count_1)` count_7,`7_max(count_id_1)` count_id_7,
        |abs(`14_max(sumall_1)`) cash_money,`14_max(count_1)` cash_user_count,`14_max(count_id_1)` cash_count
        |from  global_temp.st_bill_type
        |union all
        |select
        | app_code,owner_code,create_date , 2 user_flag ,
        |`40_max(sumall_2)` sumall_40,`40_max(count_2)` count_40,`40_max(count_id_2)` count_id_40,
        |`36_max(sumall_2)` sumall_36,`36_max(count_2)` count_36,`36_max(count_id_2)` count_id_36,
        |`37_max(sumall_2)` sumall_37,`37_max(count_2)` count_37,`37_max(count_id_2)` count_id_37,
        |`19_max(sumall_2)` sumall_19,`19_max(count_2)` count_19,`19_max(count_id_2)` count_id_19,
        |`20_max(sumall_2)` sumall_20,`20_max(count_2)` count_20,`20_max(count_id_2)` count_id_20,
        |`21_max(sumall_2)` sumall_21,`21_max(count_2)` count_21,`21_max(count_id_2)` count_id_21,
        |`22_max(sumall_2)` sumall_22,`22_max(count_2)` count_22,`22_max(count_id_2)` count_id_22,
        |`23_max(sumall_2)` sumall_23,`23_max(count_2)` count_23,`23_max(count_id_2)` count_id_23,
        |`16_max(sumall_2)` sumall_16,`16_max(count_2)` count_16,`16_max(count_id_2)` count_id_16,
        |`27_max(sumall_2)` sumall_27,`27_max(count_2)` count_27,`27_max(count_id_2)` count_id_27,
        |`28_max(sumall_2)` sumall_28,`28_max(count_2)` count_28,`28_max(count_id_2)` count_id_28,
        |`30_max(sumall_2)` sumall_30,`30_max(count_2)` count_30,`30_max(count_id_2)` count_id_30,
        |`43_max(sumall_2)` sumall_43,`43_max(count_2)` count_43,`43_max(count_id_2)` count_id_43,
        |`32_max(sumall_2)` sumall_32,`32_max(count_2)` count_32,`32_max(count_id_2)` count_id_32,
        |`44_max(sumall_2)` sumall_44,`44_max(count_2)` count_44,`44_max(count_id_2)` count_id_44,
        |`45_max(sumall_2)` sumall_45,`45_max(count_2)` count_45,`45_max(count_id_2)` count_id_45,
        |`8_max(sumall_2)` sumall_8,`8_max(count_2)` count_8,`8_max(count_id_2)` count_id_8,
        |`31_max(sumall_2)` sumall_31,`31_max(count_2)` count_31,`31_max(count_id_2)` count_id_31,
        |`9_max(sumall_2)` sumall_9,`9_max(count_2)` count_9,`9_max(count_id_2)` count_id_9,
        |`38_max(sumall_2)` sumall_38,`38_max(count_2)` count_38,`38_max(count_id_2)` count_id_38,
        |`7_max(sumall_2)` sumall_7,`7_max(count_2)` count_7,`7_max(count_id_2)` count_id_7,
        |abs(`14_max(sumall_2)`) cash_money,`14_max(count_2)` cash_user_count,`14_max(count_id_2)` cash_count
        |from  global_temp.st_bill_type
        |union all
        |select
        |app_code,owner_code,create_date , 3 user_flag ,
        |`40_max(sumall_3)` sumall_40,`40_max(count_3)` count_40,`40_max(count_id_3)` count_id_40,
        |`36_max(sumall_3)` sumall_36,`36_max(count_3)` count_36,`36_max(count_id_3)` count_id_36,
        |`37_max(sumall_3)` sumall_37,`37_max(count_3)` count_37,`37_max(count_id_3)` count_id_37,
        |`19_max(sumall_3)` sumall_19,`19_max(count_3)` count_19,`19_max(count_id_3)` count_id_19,
        |`20_max(sumall_3)` sumall_20,`20_max(count_3)` count_20,`20_max(count_id_3)` count_id_20,
        |`21_max(sumall_3)` sumall_21,`21_max(count_3)` count_21,`21_max(count_id_3)` count_id_21,
        |`22_max(sumall_3)` sumall_22,`22_max(count_3)` count_22,`22_max(count_id_3)` count_id_22,
        |`23_max(sumall_3)` sumall_23,`23_max(count_3)` count_23,`23_max(count_id_3)` count_id_23,
        |`16_max(sumall_3)` sumall_16,`16_max(count_3)` count_16,`16_max(count_id_3)` count_id_16,
        |`27_max(sumall_3)` sumall_27,`27_max(count_3)` count_27,`27_max(count_id_3)` count_id_27,
        |`28_max(sumall_3)` sumall_28,`28_max(count_3)` count_28,`28_max(count_id_3)` count_id_28,
        |`30_max(sumall_3)` sumall_30,`30_max(count_3)` count_30,`30_max(count_id_3)` count_id_30,
        |`43_max(sumall_3)` sumall_43,`43_max(count_3)` count_43,`43_max(count_id_3)` count_id_43,
        |`32_max(sumall_3)` sumall_32,`32_max(count_3)` count_32,`32_max(count_id_3)` count_id_32,
        |`44_max(sumall_3)` sumall_44,`44_max(count_3)` count_44,`44_max(count_id_3)` count_id_44,
        |`45_max(sumall_3)` sumall_45,`45_max(count_3)` count_45,`45_max(count_id_3)` count_id_45,
        |`8_max(sumall_3)` sumall_8,`8_max(count_3)` count_8,`8_max(count_id_3)` count_id_8,
        |`31_max(sumall_3)` sumall_31,`31_max(count_3)` count_31,`31_max(count_id_3)` count_id_31,
        |`9_max(sumall_3)` sumall_9,`9_max(count_3)` count_9,`9_max(count_id_3)` count_id_9,
        |`38_max(sumall_3)` sumall_38,`38_max(count_3)` count_38,`38_max(count_id_3)` count_id_38,
        |`7_max(sumall_3)` sumall_7,`7_max(count_3)` count_7,`7_max(count_id_3)` count_id_7,
        |abs(`14_max(sumall_3)`) cash_money,`14_max(count_3)` cash_user_count,`14_max(count_id_3)` cash_count
        |from  global_temp.st_bill_type
      """.stripMargin

    val df_union = sparkSession.sql(sql_union)
    df_rz_bill_type.unpersist()
    df_union
  }


}
