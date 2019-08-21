package com.square.jobs.dao

import org.apache.spark.sql.{DataFrame, SparkSession}

object UserDao {

  def getUserSt(from: String, to: String, sparkSession: SparkSession): DataFrame = {
    //  按天统计
    val sql_st_day_pro =
      s"""
         |select   app_code,owner_code,to_date(create_time) create_date ,
         |count(distinct user_id) regi_user_count_1,
         |count(distinct if(is_real,user_id,null)) regi_user_count_2,
         |count(distinct if(is_real,null,user_id)) regi_user_count_3
         |from  global_temp.sk_user_baseinfo
         |where  user_id>=0  and  create_time >='$from'
         |group  by   app_code,owner_code,to_date(create_time)
      """.stripMargin
    val df_st_day_pro = sparkSession.sql(sql_st_day_pro)
    df_st_day_pro.createOrReplaceGlobalTempView("st_day_user_pro")
    df_st_day_pro.show()

    //  列转行 : 区分 真实玩家 和 内部号
    val sql_st_day =
      """
        |select s.app_code,s.owner_code,s.create_date,s.user_flag,IFNULL(regi_user_count, 0) regi_user_count  from
        |global_temp.st_schema s
        |left join
        |(
        |    select app_code,owner_code, create_date,1 user_flag ,regi_user_count_1 regi_user_count from  global_temp.st_day_user_pro
        |    union
        |    select app_code,owner_code, create_date,2 user_flag ,regi_user_count_2 regi_user_count from  global_temp.st_day_user_pro
        |    union
        |    select app_code,owner_code, create_date,3 user_flag ,regi_user_count_3 regi_user_count from  global_temp.st_day_user_pro
        | ) st on  s.app_code=st.app_code and   s.owner_code=st.owner_code  and  s.create_date=st.create_date  and  s.user_flag=st.user_flag
      """.stripMargin
    val df_st_day = sparkSession.sql(sql_st_day)
    df_st_day.createOrReplaceGlobalTempView("st_day_user")

    //  根据历史数据 统计累计用户
    val sql_st_all =
      s"""
         |select   st2.app_code,st2.owner_code,st2.create_date,st2.user_flag,  st2.regi_user_count,  st2.regi_user_count_all from
         |(
         |    select app_code,owner_code,create_date,user_flag,regi_user_count,sum(regi_user_count) over (partition by  app_code,owner_code,user_flag  order by create_date asc)  regi_user_count_all from
         |    (
         |    select app_code,owner_code,to_date(create_date) create_date,user_flag,regi_user_count from  global_temp.st_day_user
         |    union
         |    select app_code,owner_code,to_date(create_date) create_date,user_flag,regi_user_count from  global_temp.st_day
         |    ) st
         | ) st2 where st2.create_date>= to_date('$from')
      """.stripMargin

    val df_st_all = sparkSession.sql(sql_st_all)

    df_st_day.unpersist()
    return df_st_all
  }
}
