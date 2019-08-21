package com.square.jobs.dao

import com.square.common.enums.EventMappingCode
import com.square.common.utils.DateUtils
import com.square.jobs.conf.DataJson
import com.square.jobs.utils.SparkEsUtils
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel

/**
  * 发包 抢包 统计：
  * 牛牛群统计：`type` = 2
  * 扫雷群统计：`type` = 1
  * 禁抢群统计：`type` = 3
  */
object RedBonusDao {
  /**
    *
    * @param from
    * @param to
    * @param sparkSession
    * @return
    */
  def getRedBonusBillDetails(from: String, to: String, sparkSession: SparkSession): DataFrame = {

    val df_sk_bonus: DataFrame = SparkEsUtils.readEsDataUtils(EventMappingCode.SK_REDBONUS, "create_time", DateUtils.addHour(from, -1), to, DataJson.SK_REDBONUS, sparkSession)
    // 25 牛牛结算   5  扫雷发包    41  禁抢发包   34   禁抢发包赔付到账   44 福利发包
    val sql =
      s"""
         |select distinct  r.* , b.is_real ,b.death_flag,b.billt_id,b.money bill_money,r.user_id,b.create_time bill_create_time from
         |global_temp.sk_redbonus r
         | join  (select * from global_temp.bill_details where  billt_id in (25,5,41,34,44))  b on  r.user_id= b.user_id and r.app_code=b.app_code  and r.owner_code=b.owner_code   and r.id=b.biz_id
         | where  b.user_id>=0
         | and b.create_time>='$from'
      """.stripMargin

    val df = sparkSession.sql(sql)
    df_sk_bonus.unpersist()
    df
  }

  /**
    *
    * @param from
    * @param to
    * @param sparkSession
    * @return
    */
  def getGrabBonusBillDetails(from: String, to: String, sparkSession: SparkSession): DataFrame = {
    val df_sk_bonus: DataFrame = SparkEsUtils.readEsDataUtils(EventMappingCode.SK_REDBONUS_GRAB, "create_time", DateUtils.addHour(from, -1), to, DataJson.SK_REDBONUS_GRAB, sparkSession)
    // 25 牛牛结算   3  扫雷抢包  4  扫雷包踩雷赔付   46  禁抢抢包  45 福利抢包
    val sql =
      s"""
         |select distinct g.* , b.is_real, b.death_flag,t.type ,t.is_real redb_is_real ,t.death_flag redb_death_flag,t.money redb_money,t.user_id redb_user_id,b.billt_id,b.biz_id,b.money bill_money ,b.user_id bill_user_id,t.create_time bill_create_time from
         |global_temp.red_bonus_details t
         |inner join global_temp.sk_redbonus_grab g on t.id = g.redb_id
         |inner join  (select *  from global_temp.bill_details where  billt_id in (24,25,3,4,46,45))  b on  g.user_id= b.user_id and g.app_code=b.app_code  and g.owner_code=b.owner_code   and  g.id=b.biz_id
         |where  g.user_id>=0  and  t.is_real != b.is_real
         |and b.create_time>='$from'
      """.stripMargin

    val df = sparkSession.sql(sql)

    df_sk_bonus.unpersist()
    df
  }

  /**
    *
    * 牛牛 ：  输赢
    * 流入 　　： 真实玩家输可　／　　内部号赢
    * 流出　　　： 真实玩家赢　　／　内部号输
    *
    * @param from
    * @param to
    * @param sparkSession
    * @return
    */
  def getPayoutNiu(from: String, to: String, sparkSession: SparkSession): DataFrame = {

    //　真实抢内部号的包 ; 内部号抢真实玩家的包
    val sql_payout_details =
      """
        |select app_code,owner_code,user_id , is_real ,biz_id ,max(bill_create_time) create_time ,sum(bill_money)  payout_amount
        |from
        |global_temp.grab_bonus_details grab
        | where redb_is_real !=is_real and type=2 and  (billt_id=24 or  billt_id=25)
        | group by app_code,owner_code,user_id ,is_real,biz_id
      """.stripMargin
    val df_payout_details = sparkSession.sql(sql_payout_details).persist(StorageLevel.MEMORY_AND_DISK)
    df_payout_details.createOrReplaceGlobalTempView("payout_details_niu")

    //24		牛牛押金支出	            负数
    //25		牛牛结算	                正数
    val sql_payout =
    """
      |select app_code,owner_code, to_date(create_time) create_date  ,
      |cast(sum (if(is_real and payout_amount < 0 ,abs(payout_amount),if((!is_real) and payout_amount > 0 , payout_amount,0))) AS DECIMAL (32, 2))  niu_payout_money_in, -- 资金流入 ; 真实玩家 赔付金额 ; 内部号 赔付金额
      |cast(sum (if(is_real and payout_amount > 0 ,payout_amount,if((!is_real) and payout_amount < 0 , abs(payout_amount),0))) AS DECIMAL (32, 2))  niu_payout_money_out -- 资金流出 ； 真实玩家 获赔金额  ；内部号获赔金额
      |from
      |global_temp.payout_details_niu
      |group  by  app_code,owner_code, to_date(create_time)
    """.stripMargin
    val df_payout = sparkSession.sql(sql_payout).persist(StorageLevel.MEMORY_AND_DISK)
    df_payout.createOrReplaceGlobalTempView("payout_st_niu")

    // 注册的表名 payout_niu
    val sql =
      """
        |select app_code,owner_code,create_date  ,1 user_flag,niu_payout_money_in,niu_payout_money_out ,0 niu_payout_money ,0  niu_get_payout_money from global_temp.payout_st_niu
        |union all
        |select app_code,owner_code,create_date  ,2 user_flag,niu_payout_money_in,niu_payout_money_out ,niu_payout_money_in   niu_payout_money ,niu_payout_money_out  niu_get_payout_money from global_temp.payout_st_niu
        |union all
        |select app_code,owner_code,create_date  ,3 user_flag,niu_payout_money_in,niu_payout_money_out ,niu_payout_money_out  niu_payout_money ,niu_payout_money_in  niu_get_payout_money from global_temp.payout_st_niu
      """.stripMargin
    val df = sparkSession.sql(sql)

    df_payout_details.unpersist()
    df_payout.unpersist()
    df
  }

  /**
    *
    * 扫雷：　踩雷的角度分析　　
    * 　　　 流入  　： 真实玩家踩雷: ( 免死赔付 ; 其他内部号赔付)
    *  　　　流出　　： 内部号踩雷( 免死赔付 ; 其他内部号赔付)
    *
    * @param from
    * @param to
    * @param sparkSession
    * @return
    */
  def getPayoutLei(from: String, to: String, sparkSession: SparkSession): DataFrame = {
    //  扫雷  踩雷赔付
    //　真实抢内部号的包 ; 内部号抢真实玩家的包
    val sql_payout_details =
    """
      |select app_code,owner_code,user_id ,biz_id ,bill_create_time ,is_real,death_flag,bill_money  payout_amount
      |from
      |global_temp.grab_bonus_details grab
      |where  redb_is_real !=is_real and type=1 and billt_id=4
    """.stripMargin
    val df_payout_details = sparkSession.sql(sql_payout_details).persist(StorageLevel.MEMORY_AND_DISK)
    df_payout_details.createOrReplaceGlobalTempView("payout_details_lei")

    //  4		扫雷包踩雷赔付	 负数
    val sql_payout =
      """
        |select app_code,owner_code, to_date(bill_create_time) create_date ,1 user_flag,
        |cast(sum (if(is_real,  if(death_flag==1,abs(payout_amount),0),0)) AS DECIMAL (32, 2)) lei_payout_money_death_in,
        |cast(sum (if(is_real,  if(death_flag==0,abs(payout_amount),0),0)) AS DECIMAL (32, 2)) lei_payout_money_other_in,
        |cast(sum (if(!is_real, if(death_flag==1,abs(payout_amount),0),0)) AS DECIMAL (32, 2)) lei_payout_money_death_out,
        |cast(sum (if(!is_real, if(death_flag==0,abs(payout_amount),0),0)) AS DECIMAL (32, 2)) lei_payout_money_other_out,
        |cast(sum(if(is_real  ,abs(payout_amount),0))  AS DECIMAL (32, 2))  lei_payout_money_2, -- 赔付金额
        |cast(sum(if(!is_real ,abs(payout_amount),0))  AS DECIMAL (32, 2))  lei_payout_money_3 -- 赔付金额
        |
        |from
        |global_temp.payout_details_lei
        |group  by  app_code,owner_code, to_date(bill_create_time)
      """.stripMargin
    val df_payout = sparkSession.sql(sql_payout).persist(StorageLevel.MEMORY_AND_DISK)
    df_payout.createOrReplaceGlobalTempView("payout_st_lei")

    val sql =
      """
        |select app_code,owner_code,create_date  ,1 user_flag,lei_payout_money_death_in,lei_payout_money_other_in,lei_payout_money_death_out,lei_payout_money_other_out , 0 lei_payout_money , 0 lei_get_payout_money from global_temp.payout_st_lei
        |union all
        |select app_code,owner_code,create_date  ,2 user_flag,lei_payout_money_death_in,lei_payout_money_other_in,lei_payout_money_death_out,lei_payout_money_other_out , lei_payout_money_2  lei_payout_money , lei_payout_money_3  lei_get_payout_money from global_temp.payout_st_lei
        |union all
        |select app_code,owner_code,create_date  ,3 user_flag,lei_payout_money_death_in,lei_payout_money_other_in,lei_payout_money_death_out,lei_payout_money_other_out , lei_payout_money_3  lei_payout_money , lei_payout_money_2  lei_get_payout_money from global_temp.payout_st_lei
      """.stripMargin

    val df = sparkSession.sql(sql)

    df_payout_details.unpersist()
    df_payout.unpersist()
    df
  }

  /**
    *
    * 禁抢 赔付
    *
    * @param from
    * @param to
    * @param sparkSession
    * @return
    */
  def getPayoutJin(from: String, to: String, sparkSession: SparkSession): DataFrame = {
    //  禁抢 ：  只有平台抢包  ，禁抢赔付支出 数据没有存入到 mysql ; 所以 ，只能站在 发包的角度 算赔付
    val sql_payout_details =
      """
        |select app_code,send.owner_code,send.user_id ,send.id ,send.bill_create_time  bill_create_time,send.is_real,send.bill_money payout_amount
        |from
        |global_temp.red_bonus_details send
        |where    send.is_real    and send.type=3 and  send.billt_id=34
      """.stripMargin
    val df_payout_details = sparkSession.sql(sql_payout_details).persist(StorageLevel.MEMORY_AND_DISK)
    df_payout_details.createOrReplaceGlobalTempView("payout_details_lei")

    // 34:  禁抢群 赔付到账（发包）  正数
    val sql_payout =
      """
        |select app_code,owner_code, to_date(bill_create_time) create_date  ,
        |cast(sum (payout_amount)AS DECIMAL (32, 2))  jin_payout_money_out
        |from
        |global_temp.payout_details_lei
        |group  by  app_code,owner_code, to_date(bill_create_time)
      """.stripMargin
    val df_payout = sparkSession.sql(sql_payout)

    df_payout_details.unpersist()

    df_payout
  }


  /**
    *
    * 扫雷：　发包的角度分析　　
    *
    * 流入  　： 扫雷 内部号抢包 :  （免死抢包 ; 其他内部号抢包）
    * 流出  　： 会员抢包金额
    *
    * @param from
    * @param to
    * @param sparkSession
    * @return
    *
    */
  def getBonusLei(from: String, to: String, sparkSession: SparkSession): DataFrame = {
    //  3		扫雷抢包  正数
    val sql =
      """
        |select app_code,owner_code ,to_date(bill_create_time) create_date,
        |cast(sum(if(!is_real,if(death_flag==1,money,0),0)) AS DECIMAL (32, 2)) lei_grab_money_death_in,
        |cast(sum(if(!is_real,if(death_flag==0,money,0),0)) AS DECIMAL (32, 2)) lei_grab_money_other_in,
        |cast(sum(if(is_real,money,0)) AS DECIMAL (32, 2)) lei_grab_money_out
        |from
        |global_temp.grab_bonus_details grab
        |where  (redb_is_real !=is_real) and type=1 and billt_id = 3
        |group  by  app_code,owner_code,to_date(bill_create_time)
      """.stripMargin
    val df = sparkSession.sql(sql)

    df
  }


  /**
    *
    * 禁抢  流入： 内部号抢包
    *
    * @param from
    * @param to
    * @param sparkSession
    * @return
    *
    */
  def getBonusJin(from: String, to: String, sparkSession: SparkSession): DataFrame = {

    //　禁抢 流入： 内部号抢包
    // 46	   禁抢抢包      正数
    val sql =
    """
      |select app_code,owner_code ,to_date(bill_create_time) create_date,
      |cast(sum(money) AS DECIMAL (32, 2)) jin_grab_money_in
      |from
      |global_temp.grab_bonus_details grab
      |where   redb_is_real  and type=3 and billt_id = 46
      |group  by  app_code,owner_code,to_date(bill_create_time)
    """.stripMargin

    val df = sparkSession.sql(sql)

    df
  }

  /**
    * 福利抢包
    *
    * @param from
    * @param to
    * @param sparkSession
    * @return
    */
  def getBonusReward(from: String, to: String, sparkSession: SparkSession): DataFrame = {

    //　福利抢包
    // 45		福利抢包  正数
    val sql =
    """
      |select app_code,owner_code ,to_date(bill_create_time) create_date,
      |cast(sum(if(!is_real,money,0)) AS DECIMAL (32, 2)) reward_grab_money_in,
      |cast(sum(if(is_real,money,0)) AS DECIMAL (32, 2))  reward_grab_money_out
      |from
      |global_temp.grab_bonus_details grab
      |where  (redb_is_real !=is_real) and billt_id = 45
      |group  by  app_code,owner_code,to_date(bill_create_time)
    """.stripMargin
    val df = sparkSession.sql(sql)


    df
  }


  /**
    * 系统调账金额
    *
    * @param from
    * @param to
    * @param sparkSession
    * @return
    */
  def getBillTransfer(from: String, to: String, sparkSession: SparkSession): DataFrame = {

    //	系统调整 　7　　正／　负
    val sql =
      """
        |select app_code,owner_code,to_date(create_time) create_date,
        |cast(sum(if(money>0 and is_real ,money,0)) AS DECIMAL (32, 2)) increase_member_money_out ,
        |cast(sum(if(money<0 and is_real,abs(money),0)) AS DECIMAL (32, 2)) reduce_member_money_In ,
        |cast(sum(if(money>0  and is_real=false ,money,0)) AS DECIMAL (32, 2))  increase_in_money ,
        |cast(sum(if(money<0  and is_real=false ,abs(money),0)) AS DECIMAL (32, 2)) reduce_in_money
        |from  global_temp.bill_details  where billt_id = 7
        |group by  app_code,owner_code,to_date(create_time)
      """.stripMargin
    val df = sparkSession.sql(sql)

    df
  }


  /**
    * 游戏发包抢包
    *
    * @param from
    * @param to
    * @param sparkSession
    * @return
    */
  def getGameSt(from: String, to: String, sparkSession: SparkSession): DataFrame = {

    //  25 牛牛结算　;  3		扫雷抢包  正数　    ； 46	   禁抢抢包      正数
    val sql_type =
      """
        |select app_code,owner_code, to_date(bill_create_time) create_date  ,type,
        |cast(sum (if(redb_is_real,money,0) ) AS DECIMAL (32, 2))  send_money_2,  -- 发包金额
        |cast(sum (if(!redb_is_real,money,0) ) AS DECIMAL (32, 2)) send_money_3,  -- 发包金额
        |cast(sum (if(is_real,money,0) ) AS DECIMAL (32, 2))  grab_money_2,  -- 抢包金额
        |cast(sum (if(!is_real,money,0) ) AS DECIMAL (32, 2)) grab_money_3,  -- 抢包金额
        |count(distinct  if(redb_is_real,redb_user_id,null)) send_count_user_2 , -- 发包人数
        |count(distinct  if(!redb_is_real,redb_user_id,null)) send_count_user_3,     --发包人数
        |count(distinct  if(is_real,user_id,null)) grab_count_user_2 ,  -- 抢包人数
        |count(distinct  if(!is_real,user_id,null)) grab_count_user_3 ,  -- 抢包人数
        |count(distinct  if(redb_is_real,redb_id,null)) send_count_2,  -- 发包次数
        |count(distinct  if(!redb_is_real,redb_id,null)) send_count_3,  -- 发包次数
        |count(distinct  if(is_real,id,null)) grab_count_2, -- 抢包次数
        |count(distinct  if(!is_real,id,null)) grab_count_3, -- 发包次数
        |count(distinct  if(is_real,user_id, if(redb_is_real,redb_user_id, null))) game_count_user_2, -- 游戏人数
        |count(distinct  if(!is_real,user_id, if(!redb_is_real,redb_user_id, null)))  game_count_user_3 -- 游戏人数
        |from
        |global_temp.grab_bonus_details
        |where   billt_id=25 or billt_id =3 or billt_id = 46
        |group  by  app_code,owner_code, to_date(bill_create_time),type
      """.stripMargin
    val df_type = sparkSession.sql(sql_type)

    val nums: List[Long] = List(1, 2, 3)
    val df_rz_type: DataFrame = df_type.groupBy("app_code", "owner_code", "create_date").pivot("type", nums)
      .max("send_money_2", "send_money_3", "grab_money_2", "grab_money_3", "send_count_user_2", "send_count_user_3", "grab_count_user_2", "grab_count_user_3", "send_count_2", "send_count_3", "grab_count_2", "grab_count_3", "game_count_user_2", "game_count_user_3").persist(StorageLevel.MEMORY_AND_DISK)
    df_rz_type.createOrReplaceGlobalTempView("st_bill_type")

    val sql =
      """
        |select app_code,owner_code,create_date, 2 user_flag ,
        |`1_max(send_money_2)` lei_send_money,`1_max(grab_money_2)` lei_grab_money,`1_max(send_count_user_2)`lei_send_count_user,`1_max(grab_count_user_2)`lei_grab_count_user,`1_max(send_count_2)`lei_send_count,`1_max(grab_count_2)` lei_grab_count,`1_max(game_count_user_2)` lei_game_count_user,
        |`2_max(send_money_2)` niu_send_money,`2_max(grab_money_2)` niu_grab_money,`2_max(send_count_user_2)` niu_send_count_user,`2_max(grab_count_user_2)` niu_grab_count_user,`2_max(send_count_2)` niu_send_count,`2_max(grab_count_2)` niu_grab_count,
        |`2_max(game_count_user_2)` niu_game_count_user,`3_max(send_money_2)` jin_send_money,`3_max(grab_money_2)` jin_grab_money,`3_max(send_count_user_2)` jin_send_count_user,`3_max(grab_count_user_2)` jin_grab_count_user,`3_max(send_count_2)` jin_send_count,`3_max(grab_count_2)` jin_grab_count,`3_max(game_count_user_2)` jin_game_count_user
        |from   global_temp.st_bill_type
        |union all
        |select app_code,owner_code,create_date, 3 user_flag ,
        |`1_max(send_money_3)` lei_send_money,`1_max(grab_money_3)` lei_grab_money,`1_max(send_count_user_3)`lei_send_count_user,`1_max(grab_count_user_3)`lei_grab_count_user,`1_max(send_count_3)`lei_send_count,`1_max(grab_count_3)` lei_grab_count,`1_max(game_count_user_3)` lei_game_count_user,
        |`2_max(send_money_3)` niu_send_money,`2_max(grab_money_3)` niu_grab_money,`2_max(send_count_user_3)` niu_send_count_user,`2_max(grab_count_user_3)` niu_grab_count_user,`2_max(send_count_3)` niu_send_count,`2_max(grab_count_3)` niu_grab_count,
        |`2_max(game_count_user_3)` niu_game_count_user,`3_max(send_money_3)` jin_send_money,`3_max(grab_money_3)` jin_grab_money,`3_max(send_count_user_3)` jin_send_count_user,`3_max(grab_count_user_3)` jin_grab_count_user,`3_max(send_count_3)` jin_send_count,`3_max(grab_count_3)` jin_grab_count,`3_max(game_count_user_3)` jin_game_count_user
        |from   global_temp.st_bill_type
      """.stripMargin

    val df = sparkSession.sql(sql)
    df_rz_type.unpersist()
    df
  }
}
