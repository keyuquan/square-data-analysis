package com.square.jobs.kpi

import com.square.common.Constants
import com.square.common.enums.{AppCode, EventMappingCode}
import com.square.common.utils.{DateUtils, EdaRouteAdmin, EsAdminUtil}
import com.square.jobs.conf.DataJson
import com.square.jobs.dao._
import com.square.jobs.data.SparkEsSkUserBaseinfo
import com.square.jobs.utils.{SchemaUtils, SparkEsUtils, SparkSessionUtils, StringUtils}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.elasticsearch.spark.rdd.EsSpark
import org.slf4j.LoggerFactory

/**
  * 发包 抢包 统计：
  * 牛牛群统计：`type` = 2
  * 扫雷群统计：`type` = 1
  * 禁抢群统计：`type` = 3
  */
object StDayMain {
  var from: String = "";
  var to: String = ""
  val timeStr = " 00:00:00"
  val logger = LoggerFactory.getLogger(StDayMain.getClass)

  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      logger.error("输入的参数不正确")
      return
    }
    from = StringUtils.replaceBlank(args(0)) + timeStr
    to = StringUtils.replaceBlank(args(1)) + timeStr

    logger.warn(s" from : '$from'  , to '$to'")
    val sparkSession: SparkSession = SparkSessionUtils.getOrCreate("DayKpiMain")

    try {
      // 创建全表
      val df_schema = SchemaUtils.CreateSchemaTable(from, to, sparkSession).persist(StorageLevel.MEMORY_AND_DISK)
      df_schema.show()
      df_schema.createOrReplaceGlobalTempView("st_schema")

      // 读取 以前的统计数据   并注册表 st_day_history
      val from_min = DateUtils.addDay(from, -100000)
      val df_st_history = SparkEsUtils.readEsDataUtils(EventMappingCode.ST_DAY, "create_date", from_min, from, DataJson.ST_DAY, sparkSession)

      //  全量读取 用户表
      val df_sk_user: DataFrame = SparkEsSkUserBaseinfo.readEsSkUserBaseinfoData(from_min, to, sparkSession)

      // 用户统计　
      val df_st_user = UserDao.getUserSt(from, to, sparkSession).persist(StorageLevel.MEMORY_AND_DISK)
      df_st_user.show()
      df_st_user.createOrReplaceGlobalTempView("st_user")

      // 支付统计
      val df_st_recharge = RechargeDao.getRechargeSt(from, to, sparkSession).persist(StorageLevel.MEMORY_AND_DISK)
      df_st_recharge.show()
      df_st_recharge.createOrReplaceGlobalTempView("st_recharge")

      //  bill 统计 (提现统计,福利统计)
      val df_bill_details: DataFrame = BillDao.getBillDetails(from, to, sparkSession).persist(StorageLevel.MEMORY_AND_DISK)
      df_bill_details.show()
      df_bill_details.createOrReplaceGlobalTempView("bill_details")

      val df_st_bill_user = BillDao.getBillStUser(from, to, sparkSession).persist(StorageLevel.MEMORY_AND_DISK)
      df_st_bill_user.show()
      df_st_bill_user.createOrReplaceGlobalTempView("st_bill_user")

      val df_st_bill = BillDao.getBillSt(from, to, sparkSession).persist(StorageLevel.MEMORY_AND_DISK)
      df_st_bill.show()
      df_st_bill.createOrReplaceGlobalTempView("st_bill")

      // 红包统计  平台盈利统计
      val df_sk_bonus_details: DataFrame = RedBonusDao.getRedBonusBillDetails(from, to, sparkSession).persist(StorageLevel.MEMORY_AND_DISK)
      df_sk_bonus_details.show()
      df_sk_bonus_details.createOrReplaceGlobalTempView("red_bonus_details")

      val df_sk_grab_bonus_details: DataFrame = RedBonusDao.getGrabBonusBillDetails(from, to, sparkSession).persist(StorageLevel.MEMORY_AND_DISK)
      df_sk_grab_bonus_details.show()
      df_sk_grab_bonus_details.createOrReplaceGlobalTempView("grab_bonus_details")

      // 游戏发包抢包
      val df_st_game = RedBonusDao.getGameSt(from, to, sparkSession).persist(StorageLevel.MEMORY_AND_DISK)
      df_st_game.show()
      df_st_game.createOrReplaceGlobalTempView("st_game")

      //  牛牛内部号赔付
      val df_payout_niu = RedBonusDao.getPayoutNiu(from, to, sparkSession).persist(StorageLevel.MEMORY_AND_DISK)
      df_payout_niu.show()
      df_payout_niu.createOrReplaceGlobalTempView("payout_niu")

      //  扫雷 内部号赔付
      val df_payout_lei = RedBonusDao.getPayoutLei(from, to, sparkSession).persist(StorageLevel.MEMORY_AND_DISK)
      df_payout_lei.show()
      df_payout_lei.createOrReplaceGlobalTempView("payout_lei")

      //  扫雷 内部号抢包
      val df_grab_lei = RedBonusDao.getBonusLei(from, to, sparkSession).persist(StorageLevel.MEMORY_AND_DISK)
      df_grab_lei.createOrReplaceGlobalTempView("grab_lei")
      df_grab_lei.show()

      //  禁抢 内部号赔付
      val df_payout_jin = RedBonusDao.getPayoutJin(from, to, sparkSession).persist(StorageLevel.MEMORY_AND_DISK)
      df_payout_jin.show()
      df_payout_jin.createOrReplaceGlobalTempView("payout_jin")

      //  禁抢 内部号抢包
      val df_grab_jin = RedBonusDao.getBonusJin(from, to, sparkSession).persist(StorageLevel.MEMORY_AND_DISK)
      df_grab_jin.createOrReplaceGlobalTempView("grab_jin")
      df_grab_jin.show()

      //  福利抢包
      val df_reward = RedBonusDao.getBonusReward(from, to, sparkSession).persist(StorageLevel.MEMORY_AND_DISK)
      df_reward.createOrReplaceGlobalTempView("grab_reward")
      df_reward.show()

      //  平台调增和调节减
      val df_transfer = RedBonusDao.getBillTransfer(from, to, sparkSession).persist(StorageLevel.MEMORY_AND_DISK)
      df_transfer.createOrReplaceGlobalTempView("platform_transfer")
      df_transfer.show()

      val sql_day_kpi =
        """
          |SELECT
          |    md5(concat(s.app_code,s.owner_code,s.create_date,s.user_flag))  id ,
          |    s.app_code,
          |    s.owner_code,
          |    concat(s.create_date,' 00:00:00') create_date,
          |    s.user_flag,
          |    IFNULL(regi_user_count, 0) regi_user_count,
          |    IFNULL(regi_user_count_all, 0) regi_user_count_all,
          |    IFNULL(recharge_user_count, 0) recharge_user_count,
          |    IFNULL(recharge_count, 0) recharge_count,
          |    IFNULL(first_recharge_count, 0) first_recharge_count,
          |    IFNULL(second_recharge_count, 0) second_recharge_count,
          |    IFNULL(renewal_recharge_count, 0) renewal_recharge_count,
          |    IFNULL(recharge_money, 0) recharge_money,
          |    IFNULL(first_recharge_money, 0) first_recharge_money,
          |    IFNULL(second_recharge_money, 0) second_recharge_money,
          |    IFNULL(renewal_recharge_money, 0) renewal_recharge_money,
          |    IFNULL(recharge_user_count_1, 0) recharge_user_count_1,
          |	   IFNULL(recharge_count_1, 0) recharge_count_1,
          |	   IFNULL(recharge_money_1, 0) recharge_money_1,
          |    IFNULL(recharge_user_count_2, 0) recharge_user_count_2,
          |    IFNULL(recharge_count_2, 0) recharge_count_2,
          |    IFNULL(recharge_money_2, 0) recharge_money_2,
          |    IFNULL(recharge_user_count_3, 0) recharge_user_count_3,
          |    IFNULL(recharge_count_3, 0) recharge_count_3,
          |    IFNULL(recharge_money_3, 0) recharge_money_3,
          |    IFNULL(cash_count, 0) cash_count,
          |    IFNULL(cash_user_count, 0) cash_user_count,
          |    IFNULL(cash_money, 0) cash_money,
          |    IFNULL(active_user_count, 0) active_user_count,
          |    IFNULL(user_profit, 0) user_profit,
          |    IFNULL(final_balance, 0) final_balance,
          |    IFNULL(initial_balance, 0) initial_balance,
          |    IFNULL(sumall_40, 0) sumall_40,
          |    IFNULL(sumall_36, 0) sumall_36,
          |    IFNULL(sumall_37, 0) sumall_37,
          |    IFNULL(sumall_19, 0) sumall_19,
          |    IFNULL(sumall_20, 0) sumall_20,
          |    IFNULL(sumall_21, 0) sumall_21,
          |    IFNULL(sumall_22, 0) sumall_22,
          |    IFNULL(sumall_23, 0) sumall_23,
          |    IFNULL(sumall_16, 0) sumall_16,
          |    IFNULL(sumall_27, 0) sumall_27,
          |    IFNULL(sumall_28, 0) sumall_28,
          |    IFNULL(sumall_30, 0) sumall_30,
          |    IFNULL(sumall_43, 0) sumall_43,
          |    IFNULL(sumall_32, 0) sumall_32,
          |    IFNULL(sumall_44, 0) sumall_44,
          |    IFNULL(sumall_45, 0) sumall_45,
          |    IFNULL(sumall_8, 0) sumall_8,
          |    IFNULL(sumall_31, 0) sumall_31,
          |    IFNULL(sumall_9, 0) sumall_9,
          |    IFNULL(sumall_38, 0) sumall_38,
          |    IFNULL(sumall_7, 0) sumall_7,
          |    IFNULL(count_id_36, 0) count_id_40,
          |    IFNULL(count_id_36, 0) count_id_36,
          |    IFNULL(count_id_37, 0) count_id_37,
          |    IFNULL(count_id_19, 0) count_id_19,
          |    IFNULL(count_id_20, 0) count_id_20,
          |    IFNULL(count_id_21, 0) count_id_21,
          |    IFNULL(count_id_22, 0) count_id_22,
          |    IFNULL(count_id_23, 0) count_id_23,
          |    IFNULL(count_id_16, 0) count_id_16,
          |    IFNULL(count_id_27, 0) count_id_27,
          |    IFNULL(count_id_28, 0) count_id_28,
          |    IFNULL(count_id_30, 0) count_id_30,
          |    IFNULL(count_id_43, 0) count_id_43,
          |    IFNULL(count_id_32, 0) count_id_32,
          |    IFNULL(count_id_44, 0) count_id_44,
          |    IFNULL(count_id_45, 0) count_id_45,
          |
          |    IFNULL(count_id_8, 0) count_id_8,
          |    IFNULL(count_id_31, 0) count_id_31,
          |    IFNULL(count_id_9, 0) count_id_9,
          |    IFNULL(count_id_38, 0) count_id_38,
          |    IFNULL(count_id_7, 0) count_id_7,
          |    IFNULL(count_40, 0) count_40,
          |    IFNULL(count_36, 0) count_36,
          |    IFNULL(count_37, 0) count_37,
          |    IFNULL(count_19, 0) count_19,
          |    IFNULL(count_20, 0) count_20,
          |    IFNULL(count_21, 0) count_21,
          |    IFNULL(count_22, 0) count_22,
          |    IFNULL(count_23, 0) count_23,
          |    IFNULL(count_16, 0) count_16,
          |    IFNULL(count_27, 0) count_27,
          |    IFNULL(count_28, 0) count_28,
          |    IFNULL(count_30, 0) count_30,
          |    IFNULL(count_43, 0) count_43,
          |    IFNULL(count_32, 0) count_32,
          |    IFNULL(count_44, 0) count_44,
          |    IFNULL(count_45, 0) count_45,
          |    IFNULL(count_8, 0) count_8,
          |    IFNULL(count_31, 0) count_31,
          |    IFNULL(count_9, 0) count_9,
          |    IFNULL(count_38, 0) count_38,
          |    IFNULL(count_7, 0) count_7,
          |    IFNULL(reward_user_count, 0) reward_user_count,
          |    IFNULL(commision_user_count, 0) commision_user_count,
          |      -- game static  jin_payout_money_out 和 jin_payout_money 完全一样
          |
          |    IFNULL(niu_payout_money,0) niu_payout_money,
          |    IFNULL(niu_get_payout_money,0) niu_get_payout_money,
          |
          |    IFNULL(lei_payout_money,0) lei_payout_money,
          |    IFNULL(lei_get_payout_money,0) lei_get_payout_money,
          |
          |    IFNULL(lei_send_money,0) lei_send_money,
          |    IFNULL(lei_grab_money,0) lei_grab_money,
          |    IFNULL(lei_send_count_user,0) lei_send_count_user,
          |    IFNULL(lei_grab_count_user,0) lei_grab_count_user,
          |    IFNULL(lei_send_count,0) lei_send_count,
          |    IFNULL(lei_grab_count,0) lei_grab_count,
          |    IFNULL(lei_game_count_user,0) lei_game_count_user,
          |    IFNULL(niu_send_money,0) niu_send_money,
          |    IFNULL(niu_grab_money,0) niu_grab_money,
          |    IFNULL(niu_send_count_user,0) niu_send_count_user,
          |    IFNULL(niu_grab_count_user,0) niu_grab_count_user,
          |    IFNULL(niu_send_count,0) niu_send_count,
          |    IFNULL(niu_grab_count,0)niu_grab_count,
          |    IFNULL(niu_game_count_user,0) niu_game_count_user,
          |    IFNULL(jin_send_money,0) jin_send_money,
          |    IFNULL(jin_grab_money,0) jin_grab_money,
          |    IFNULL(jin_send_count_user,0) jin_send_count_user,
          |    IFNULL(jin_grab_count_user,0) jin_grab_count_user,
          |    IFNULL(jin_send_count,0) jin_send_count,
          |    IFNULL(jin_grab_count,0) jin_grab_count,
          |    IFNULL(jin_game_count_user,0) jin_game_count_user,
          |
          |     -- plat_profit
          |
          |    IFNULL(niu_payout_money_in, 0) niu_payout_money_in,
          |    IFNULL(niu_payout_money_out, 0) niu_payout_money_out,
          |    IFNULL(lei_grab_money_out, 0) lei_grab_money_out,
          |    IFNULL(lei_payout_money_death_in, 0) lei_payout_money_death_in,
          |    IFNULL(lei_payout_money_other_in, 0) lei_payout_money_other_in,
          |    IFNULL(lei_grab_money_death_in, 0) lei_grab_money_death_in,
          |    IFNULL(lei_payout_money_death_out, 0) lei_payout_money_death_out,
          |    IFNULL(lei_grab_money_other_in, 0) lei_grab_money_other_in,
          |    IFNULL(lei_payout_money_other_out, 0) lei_payout_money_other_out,
          |    IFNULL(jin_payout_money_out, 0) jin_payout_money_out,
          |    IFNULL(jin_grab_money_in, 0) jin_grab_money_in,
          |    IFNULL(reward_grab_money_in, 0) reward_grab_money_in,
          |    IFNULL(reward_grab_money_out, 0) reward_grab_money_out,
          |    IFNULL(increase_member_money_out, 0) increase_member_money_out,
          |    IFNULL(reduce_member_money_In, 0) reduce_member_money_In,
          |    IFNULL(increase_in_money, 0) increase_in_money,
          |    IFNULL(reduce_in_money, 0) reduce_in_money,
          |    (abs(IFNULL(sumall_40, 0))+IFNULL(niu_payout_money_in, 0)+IFNULL(lei_payout_money_death_in, 0)+IFNULL(lei_payout_money_other_in, 0)+IFNULL(lei_grab_money_death_in, 0)+IFNULL(lei_grab_money_other_in, 0)+IFNULL(jin_grab_money_in, 0)+abs(IFNULL(sumall_36, 0))+IFNULL(reward_grab_money_in, 0)+IFNULL(reduce_member_money_In, 0)) platform_money_in,
          |    (IFNULL(niu_payout_money_out, 0)+IFNULL(lei_grab_money_out, 0)+IFNULL(lei_payout_money_death_out, 0)+IFNULL(lei_payout_money_other_out, 0)+IFNULL(jin_payout_money_out, 0)+abs(IFNULL(sumall_37, 0)) +IFNULL(reward_grab_money_out, 0)+abs(IFNULL(sumall_32, 0)) + abs(IFNULL(sumall_19, 0))+ abs(IFNULL(sumall_20, 0))+ abs(IFNULL(sumall_27, 0))+ abs(IFNULL(sumall_28, 0))+ abs(IFNULL(sumall_30, 0))+ abs(IFNULL(sumall_21, 0))+ abs(IFNULL(sumall_22, 0))+ abs(IFNULL(sumall_23, 0))+ abs(IFNULL(sumall_16, 0)) + abs(IFNULL(sumall_43, 0))+ abs(IFNULL(sumall_8, 0))+ abs(IFNULL(sumall_9, 0))+ abs(IFNULL(sumall_31, 0))+ abs(IFNULL(sumall_38, 0)) +IFNULL(increase_member_money_out, 0))  platform_money_out
          |FROM
          |    global_temp.st_schema s
          |    left join  global_temp.st_user uer on  s.app_code=uer.app_code and   s.owner_code=uer.owner_code  and  s.create_date=uer.create_date  and  s.user_flag=uer.user_flag
          |    left join  global_temp.st_recharge recharge on  s.app_code=recharge.app_code and   s.owner_code=recharge.owner_code  and  s.create_date=recharge.create_date and  s.user_flag=recharge.user_flag
          |    left join  global_temp.st_bill_user bill_game on  s.app_code=bill_game.app_code and   s.owner_code=bill_game.owner_code  and  s.create_date=bill_game.create_date  and  s.user_flag=bill_game.user_flag
          |    left join  global_temp.st_bill   sb on  s.app_code=sb.app_code and   s.owner_code=sb.owner_code  and  s.create_date=sb.create_date  and  s.user_flag=sb.user_flag
          |    left join  global_temp.payout_niu pn on  s.app_code=pn.app_code and   s.owner_code=pn.owner_code  and  s.create_date=pn.create_date  and  s.user_flag=pn.user_flag
          |    left join  global_temp.payout_lei pl on  s.app_code=pl.app_code and   s.owner_code=pl.owner_code  and  s.create_date=pl.create_date  and  s.user_flag=pl.user_flag
          |    left join  global_temp.grab_lei gl   on  s.app_code=gl.app_code and   s.owner_code=gl.owner_code  and  s.create_date=gl.create_date
          |    left join  global_temp.payout_jin pj on  s.app_code=pj.app_code and   s.owner_code=pj.owner_code  and  s.create_date=pj.create_date
          |    left join  global_temp.grab_jin gj   on  s.app_code=gj.app_code and   s.owner_code=gj.owner_code  and  s.create_date=gj.create_date
          |    left join  global_temp.platform_transfer pt   on  s.app_code=pt.app_code and   s.owner_code=pt.owner_code  and  s.create_date=pt.create_date
          |    left join  global_temp.grab_reward gr   on  s.app_code=gr.app_code and   s.owner_code=gr.owner_code  and  s.create_date=gr.create_date
          |    left join  global_temp.st_game gs   on  s.app_code=gs.app_code and   s.owner_code=gs.owner_code  and  s.create_date=gs.create_date  and  s.user_flag=gs.user_flag
          |    where  s.owner_code<>''
          |    """.stripMargin
      val df_day_kpi = sparkSession.sql(sql_day_kpi).persist(StorageLevel.MEMORY_AND_DISK)
      df_day_kpi.show(100000)

      val index_user_pay = EdaRouteAdmin.getOrCreateIndex(AppCode.REDBONUS.getValue, Constants.ST_DAY_TYPE, Constants.ST_DAY_MAPPING)
      EsSpark.saveJsonToEs(df_day_kpi.toJSON.rdd, index_user_pay.getIndexName + "/" + Constants.ST_DAY_TYPE, Map("es.mapping.id" -> "id"))
      EdaRouteAdmin.updateRouteIndex(AppCode.REDBONUS.getValue, Constants.ST_DAY_TYPE, Constants.ST_DAY_MAPPING, index_user_pay, "create_date")

      df_st_user.unpersist()
      df_st_recharge.unpersist()
      df_day_kpi.unpersist()
      df_sk_user.unpersist()
      df_st_history.unpersist()
      df_schema.unpersist()
      df_sk_bonus_details.unpersist()
      df_sk_grab_bonus_details.unpersist()
      df_bill_details.unpersist()
      df_st_bill.unpersist()
      df_payout_niu.unpersist()
      df_payout_lei.unpersist()
      df_grab_lei.unpersist()
      df_payout_jin.unpersist()
      df_grab_jin.unpersist()
      df_transfer.unpersist()
      df_reward.unpersist()

    } catch {
      case e: Exception => logger.error("execute error.", e)
    } finally {

      sparkSession.stop()
      EsAdminUtil.closeClient()
    }
  }
}
