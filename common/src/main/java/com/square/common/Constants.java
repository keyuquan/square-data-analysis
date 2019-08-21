package com.square.common;


/**
 * Created by admin on 2018/10/08.
 */
public interface Constants {
    // mysql 操作
    String MYSQL_INSERT = "INSERT";
    String MYSQL_UPDATE = "UPDATE";

    // kafka conf
    String CONSUMER_GROUP_ID = "eda_realtime";
    String CONSUMER_RE_DATA_GROUP_ID = "eda_realtime_re_data";
    // es conf
    int ES_PORT = 9200;
    String ES_SCHEME = "http";
    String ROUTE_INDEX_NAME = "eda_index_route";
    String ROUTE_TYPE_NAME = "eda_index_route";
    long INDEX_MAX_SIZE = 100000000l;
    long INDEX_MAX_ONE_SIZ = 10000000000l;
    //  mapping conf
    String ROUTE_INDEX_MAPPING = "{\n" +
            "        \"properties\":{\n" +
            "            \"app_code\":{\n" +
            "                \"type\":\"keyword\"\n" +
            "            },\n" +
            "            \"event_code\":{\n" +
            "                \"type\":\"keyword\"\n" +
            "            },\n" +
            "            \"index_name\":{\n" +
            "                \"type\":\"keyword\"\n" +
            "            },\n" +
            "            \"start_time\":{\n" +
            "                \"type\":\"date\",\n" +
            "                \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "            },\n" +
            "            \"end_time\":{\n" +
            "                \"type\":\"date\",\n" +
            "                \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "            },\n" +
            "            \"size\":{\n" +
            "                \"type\":\"long\"\n" +
            "            }\n" +
            "        }\n" +
            "}";

    String SK_BILL_MAPPING = "{\n" +
            "    \"properties\":{\n" +
            "       \"id\":{\n" +
            "          \"type\":\"long\",\n" +
            "          \"index\": false\n" +
            "        },\n" +
            "        \"user_id\":{\n" +
            "           \"type\":\"long\"\n" +
            "        },\n" +
            "         \"billt_id\":{\n" +
            "           \"type\":\"long\"\n" +
            "        },\n" +
            "         \"money\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "         \"money_before\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "         \"money_after\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "         \"create_time\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "         \"create_by\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "         \"biz_id\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "         \"unique_id\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"intro\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"app_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"owner_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"event_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        }\n" +
            "    }      \n" +
            "}";

    String SK_REDBONUS_MAPPING = "{\n" +
            "    \"properties\":{\n" +
            "       \"id\":{\n" +
            "          \"type\":\"long\",\n" +
            "          \"index\": false\n" +
            "        },\n" +
            "        \"user_id\":{\n" +
            "           \"type\":\"long\"\n" +
            "        },\n" +
            "         \"chatg_id\":{\n" +
            "           \"type\":\"long\"\n" +
            "        },\n" +
            "         \"bill_id\":{\n" +
            "           \"type\":\"long\"\n" +
            "        },\n" +
            "         \"money\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "         \"total\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "         \"type\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "         \"ip\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"create_time\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "          \"version\":{\n" +
            "           \"type\":\"long\"\n" +
            "        },\n" +
            "         \"unique_id\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"app_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"owner_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"event_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        }\n" +
            "    } " +
            "}";


    String SK_REDBONUS_GRAB_MAPPING = "{\n" +
            "    \"properties\":{\n" +
            "       \"id\":{\n" +
            "          \"type\":\"long\",\n" +
            "          \"index\": false\n" +
            "        },\n" +
            "        \"user_id\":{\n" +
            "           \"type\":\"long\"\n" +
            "        },\n" +
            "         \"redb_id\":{\n" +
            "           \"type\":\"long\"\n" +
            "        },\n" +
            "        \"bill_id\":{\n" +
            "           \"type\":\"long\"\n" +
            "        },\n" +
            "         \"money\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "          \"order\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "         \"create_time\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "         \"create_by\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "         \"unique_id\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"app_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"owner_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"event_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        }\n" +
            "    }      \n" +
            "}\n";

    // 账单统计
    String SK_BILL_STATISTICS_TYPE = "st_bill";
    String SK_BILL_STATISTICS_MAPPING = "{\n" +
            "    \"properties\":{\n" +
            "       \"id\":{\n" +
            "          \"type\":\"keyword\",\n" +
            "          \"index\": false\n" +
            "        },\n" +
            "        \"app_code\":{\n" +
            "           \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"owner_code\":{\n" +
            "           \"type\":\"keyword\"\n" +
            "        },\n" +
            "          \"create_date\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "        \"billt_id\":{\n" +
            "           \"type\":\"long\"\n" +
            "        },\n" +
            "         \"user_count\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "          \"money\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "       \n" +
            "         \"bill_count\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "         \"bill_plus_count\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        }\n" +
            "    }      \n" +
            "}";

    // 发包统计
    String SK_REDBONUS_STATISTICS_TYPE = "st_redbonus";
    String SK_BILL_REDBONUS_STATISTICS_MAPPING = "{\n" +
            "    \"properties\":{\n" +
            "       \"id\":{\n" +
            "          \"type\":\"keyword\",\n" +
            "          \"index\": false\n" +
            "        },\n" +
            "        \"app_code\":{\n" +
            "           \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"owner_code\":{\n" +
            "           \"type\":\"keyword\"\n" +
            "        },\n" +
            "          \"create_date\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "        \"type\":{\n" +
            "           \"type\":\"integer\"\n" +
            "        },\n" +
            "         \"user_count\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "       \"count\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "          \"money\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "         \"grab_user_count\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "       \"grab_count\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "          \"grab_money\":{\n" +
            "            \"type\":\"double\"\n" +
            "        }\n" +
            "    }      \n" +
            "}";

    String SK_FINANCE_CASH_DRAWS_MAPPING = "{\n" +
            "    \"properties\":{\n" +
            "        \"id\":{\n" +
            "            \"type\":\"long\",\n" +
            "             \"index\": false\n" +
            "        },\n" +
            "        \"bill_id\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"merchant_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"target_id\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"upay_id\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"bank_name\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"bank_no\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"bank_user\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"bank_region\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"cash_ip\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"before_money\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"money\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"cash_no\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"operator_id\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"operator_ip\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"operator_time\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "        \"handle_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"success_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"remark\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"create_time\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "        \"status\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"create_by\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"del_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"last_update_by\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"last_update_time\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "        \"version\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"cause\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"unique_id\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"app_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"owner_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"event_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        }\n" +
            "    }\n" +
            "}";


    String SK_FINANCE_RECHARGE_MAPPING = "{\n" +
            "    \"properties\":{\n" +
            "        \"id\":{\n" +
            "            \"type\":\"long\",\n" +
            "             \"index\": false\n" +
            "        },\n" +
            "        \"local_no\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"user_id\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"amount\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"first_recharge_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"bef_rechange_balance\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"type\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"chanel_id\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"chanel_name\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"description\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"payee_name\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"bank_remark\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"cause\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"out_trade_no\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"drop_order_flag\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"deal_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"paid_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"paid_id\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"paid_time\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "        \"audit_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"audit_id\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"audited_time\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "        \"creater_id\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"create_time\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "        \"last_update_time\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "        \"del_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"version\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"bank_recharge_type\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"bank_id\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"bank_user\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"bank_last_no\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"valid_time\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "        \"valid_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"payee_id\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"pay_bean\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"bank_name\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"unique_id\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"app_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"owner_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"event_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    String SK_USER_BASEINFO_MAPPING = "{\n" +
            "    \"properties\":{\n" +
            "        \"id\":{\n" +
            "            \"type\":\"long\",\n" +
            "             \"index\": false\n" +
            "        },\n" +
            "        \"user_id\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"nick\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"email\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"avatar\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"gender\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"birthday\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "        \"invitecode\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"create_time\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "        \"status\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"robot_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"death_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"shutup_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"invite_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"new_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"shutup_cause\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"del_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"create_by\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"last_update_by\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"last_update_time\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "        \"forbid_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"innernum_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"forbid_cause\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"mobile\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"password\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"salt\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"im_token\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"agent_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"groupowen_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"manager_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"register_ip\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"login_ip\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"unique_id\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"app_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"owner_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "         \"event_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        }\n" +
            "    }\n" +
            "}";


    // 账单统计
    String st_payout_active_STATISTICS_TYPE = "st_payout_active";
    String st_payout_active_STATISTICS_MAPPING = "{\n" +
            "    \"properties\":{\n" +
            "        \"id\":{\n" +
            "            \"type\":\"keyword\",\n" +
            "             \"index\": false\n" +
            "        },\n" +
            "        \"app_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"owner_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"create_date\":{\n" +
            "            \"type\":\"date\"\n" +
            "             \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "        \"status_all\":{\n" +
            "            \"type\":\"long\"\n" +
            "        },\n" +
            "        \"sumall\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"countall\":{\n" +
            "            \"type\":\"long\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    // 按天统计
    String ST_DAY_TYPE = "st_day";
    String ST_DAY_MAPPING = "{\n" +
            "  \"properties\":{\n" +
            "    \"id\":{\"type\":\"keyword\",\"index\": false},\n" +
            "    \"app_code\":{\"type\":\"keyword\"},\n" +
            "    \"owner_code\":{\"type\":\"keyword\"},\n" +
            "    \"create_date\":{\"type\":\"date\",\"format\":\"yyyy-MM-dd HH:mm:ss\"},\n" +
            "    \"regi_user_count\":{\"type\":\"integer\"},\n" +
            "    \"regi_user_count_all\":{\"type\":\"integer\" },\n" +
            "    \"recharge_user_count\":{\"type\":\"integer\"},\n" +
            "    \"recharge_count\":{\"type\":\"integer\"},\n" +
            "    \"first_recharge_count\":{\"type\":\"integer\"},\n" +
            "    \"second_recharge_count\":{\"type\":\"integer\"},\n" +
            "    \"recharge_money\":{\"type\":\"integer\"},\n" +
            "    \"first_recharge_money\":{\"type\":\"integer\"},\n" +
            "    \"second_recharge_money\":{\"type\":\"integer\"},\n" +
            "    \"cash_count\":{\"type\":\"integer\"},\n" +
            "    \"cash_money\":{\"type\":\"double\"},\n" +
            "    \"active_user_count\":{\"type\":\"integer\"},\n" +
            "    \"user_profit\":{\"type\":\"double\"},\n" +
            "    \"final_balance\":{\"type\":\"double\"},\n" +
            "    \"initial_balance\":{\"type\":\"double\"},\n" +
            "    \"sumall_40\":{\"type\":\"double\"},\n" +
            "    \"sumall_36\":{\"type\":\"double\"},\n" +
            "    \"sumall_37\":{\"type\":\"double\"},\n" +
            "    \"sumall_19\":{\"type\":\"double\"},\n" +
            "    \"sumall_20\":{\"type\":\"double\"},\n" +
            "    \"sumall_21\":{\"type\":\"double\"},\n" +
            "    \"sumall_22\":{\"type\":\"double\"},\n" +
            "    \"sumall_23\":{\"type\":\"double\"},\n" +
            "    \"sumall_16\":{\"type\":\"double\"},\n" +
            "    \"sumall_27\":{\"type\":\"double\"},\n" +
            "    \"sumall_28\":{\"type\":\"double\"},\n" +
            "    \"sumall_30\":{\"type\":\"double\"},\n" +
            "    \"sumall_43\":{\"type\":\"double\"},\n" +
            "    \"sumall_32\":{\"type\":\"double\"},\n" +
            "    \"sumall_44\":{\"type\":\"double\"},\n" +
            "    \"sumall_45\":{\"type\":\"double\"},\n" +
            "    \"count_40\":{\"type\":\"integer\"},\n" +
            "    \"count_36\":{\"type\":\"integer\"},\n" +
            "    \"count_37\":{\"type\":\"integer\"},\n" +
            "    \"count_19\":{\"type\":\"integer\"},\n" +
            "    \"count_20\":{\"type\":\"integer\"},\n" +
            "    \"count_21\":{\"type\":\"integer\"},\n" +
            "    \"count_22\":{\"type\":\"integer\"},\n" +
            "    \"count_23\":{\"type\":\"integer\"},\n" +
            "    \"count_16\":{\"type\":\"integer\"},\n" +
            "    \"count_27\":{\"type\":\"integer\"},\n" +
            "    \"count_28\":{\"type\":\"integer\"},\n" +
            "    \"count_30\":{\"type\":\"integer\"},\n" +
            "    \"count_43\":{\"type\":\"integer\"},\n" +
            "    \"count_32\":{\"type\":\"integer\"},\n" +
            "    \"count_44\":{\"type\":\"integer\"},\n" +
            "    \"count_45\":{\"type\":\"integer\"},\n" +
            "    \"count_id_40\":{\"type\":\"integer\"},\n" +
            "    \"count_id_36\":{\"type\":\"integer\"},\n" +
            "    \"count_id_37\":{\"type\":\"integer\"},\n" +
            "    \"count_id_19\":{\"type\":\"integer\"},\n" +
            "    \"count_id_20\":{\"type\":\"integer\"},\n" +
            "    \"count_id_21\":{\"type\":\"integer\"},\n" +
            "    \"count_id_22\":{\"type\":\"integer\"},\n" +
            "    \"count_id_23\":{\"type\":\"integer\"},\n" +
            "    \"count_id_16\":{\"type\":\"integer\"},\n" +
            "    \"count_id_27\":{\"type\":\"integer\"},\n" +
            "    \"count_id_28\":{\"type\":\"integer\"},\n" +
            "    \"count_id_30\":{\"type\":\"integer\"},\n" +
            "    \"count_id_43\":{\"type\":\"integer\"},\n" +
            "    \"count_id_32\":{\"type\":\"integer\"},\n" +
            "    \"count_id_44\":{\"type\":\"integer\"},\n" +
            "    \"count_id_45\":{\"type\":\"integer\"},\n" +
            "   \"lei_payout_money\":{\"type\":\"double\"},\n" +
            "   \"lei_get_payout_money\":{\"type\":\"double\"},\n" +
            "   \"lei_send_money\":{\"type\":\"double\"},\n" +
            "   \"lei_grab_money\":{\"type\":\"double\"},\n" +
            "   \"lei_send_count_user\":{\"type\":\"integer\"},\n" +
            "   \"lei_grab_count_user\":{\"type\":\"integer\"},\n" +
            "   \"lei_send_count\":{\"type\":\"integer\"},\n" +
            "   \"lei_grab_count\":{\"type\":\"integer\"},\n" +
            "   \"lei_game_count_user\":{\"type\":\"integer\"},\n" +
            "   \"niu_payout_money\":{\"type\":\"double\"},\n" +
            "   \"niu_get_payout_money\":{\"type\":\"double\"},\n" +
            "   \"niu_send_money\":{\"type\":\"double\"},\n" +
            "   \"niu_grab_money\":{\"type\":\"double\"},\n" +
            "   \"niu_send_count_user\":{\"type\":\"integer\"},\n" +
            "   \"niu_grab_count_user\":{\"type\":\"integer\"},\n" +
            "   \"niu_send_count\":{\"type\":\"integer\"},\n" +
            "   \"niu_grab_count\":{\"type\":\"integer\"},\n" +
            "   \"niu_game_count_user\":{\"type\":\"integer\"},\n" +
            "   \"jin_payout_money\":{\"type\":\"double\"},\n" +
            "   \"jin_send_money\":{\"type\":\"double\"},\n" +
            "   \"jin_grab_money\":{\"type\":\"double\"},\n" +
            "   \"jin_send_count_user\":{\"type\":\"integer\"},\n" +
            "   \"jin_grab_count_user\":{\"type\":\"integer\"},\n" +
            "   \"jin_send_count\":{\"type\":\"integer\"},\n" +
            "   \"jin_grab_count\":{\"type\":\"integer\"},\n" +
            "   \"jin_game_count_user\":{\"type\":\"integer\"},   \n" +
            "   \"lei_grab_money_out\":{\"type\":\"double\"},\n" +
            "   \"lei_payout_money_death_in\":{\"type\":\"double\"},\n" +
            "   \"lei_payout_money_other_in\":{\"type\":\"double\"},\n" +
            "   \"lei_grab_money_death_in\":{\"type\":\"double\"},\n" +
            "   \"lei_payout_money_death_out\":{\"type\":\"double\"},\n" +
            "   \"lei_grab_money_other_in\":{\"type\":\"double\"},\n" +
            "   \"lei_payout_money_other_out\":{\"type\":\"double\"},\n" +
            "   \"jin_payout_money_out\":{\"type\":\"double\"},\n" +
            "   \"jin_grab_money_in\":{\"type\":\"double\"},\n" +
            "   \"reward_grab_money_in\":{\"type\":\"double\"},\n" +
            "   \"reward_grab_money_out\":{\"type\":\"double\"},\n" +
            "   \"increase_member_money_out\":{\"type\":\"double\"},\n" +
            "   \"reduce_member_money_In\":{\"type\":\"double\"},\n" +
            "   \"increase_in_money\":{\"type\":\"double\"},\n" +
            "   \"reduce_in_money\":{\"type\":\"double\"},\n" +
            "   \"platform_money_in\":{\"type\":\"double\"},\n" +
            "   \"platform_money_out\":{\"type\":\"double\"}\n" +
            "  }      \n" +
            "}\n";


    // 真实玩家、内部号、所有玩家分别统计总金额、人数、次数
    String ST_BASIS_STATISTICS_TYPE = "st_basis";
    String ST_BASIS_STATISTICS_MAPPING = "{\n" +
            "    \"properties\":{\n" +
            "        \"id\":{\n" +
            "            \"type\":\"keyword\",\n" +
            "            \"index\":false\n" +
            "        },\n" +
            "        \"app_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"owner_code\":{\n" +
            "            \"type\":\"keyword\"\n" +
            "        },\n" +
            "        \"create_date\":{\n" +
            "            \"type\":\"date\",\n" +
            "            \"format\":\"yyyy-MM-dd HH:mm:ss\"\n" +
            "        },\n" +
            "        \"user_flag\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"sumall_24\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_25\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_40\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_3\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_5\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_4\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_6\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_18\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_41\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_34\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_36\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_37\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_19\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_20\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_21\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_22\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_23\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_16\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_27\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_28\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_30\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_43\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_32\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_44\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_45\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_8\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_31\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_9\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"sumall_38\":{\n" +
            "            \"type\":\"double\"\n" +
            "        },\n" +
            "        \"count_id_24\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_25\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_40\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_3\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_5\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_4\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_6\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_18\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_41\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_34\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_36\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_37\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_19\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_20\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_21\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_22\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_23\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_16\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_27\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_28\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_30\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_43\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_32\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_44\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_45\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_8\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_31\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_9\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_id_38\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_24\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_25\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_40\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_3\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_5\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_4\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_6\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_18\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_41\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_34\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_36\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_37\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_19\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_20\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_21\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_22\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_23\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_16\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_27\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_28\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_30\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_43\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_32\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_44\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_45\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_8\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_31\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_9\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        },\n" +
            "        \"count_38\":{\n" +
            "            \"type\":\"integer\"\n" +
            "        }\n" +
            "    }\n" +
            "}\n";

    String OWNER_ALL_TYPE = "owner_all";
    String OWNER_MAPPING = "{\n" +
            "  \"properties\":{\n" +
            "    \"id\":{\"type\":\"keyword\",\"index\": false},\n" +
            "    \"app_code\":{\"type\":\"keyword\"},\n" +
            "    \"owner_code\":{\"type\":\"keyword\"},\n" +
            "    \"create_date\":{\"type\":\"date\",\"format\":\"yyyy-MM-dd HH:mm:ss\"}\n" +
            "  }      \n" +
            "}";

    // 首充 二充明细
    String RECHARGE_DETAILS_TYPE = "details_recharge";
    String RECHARGE_DETAILS_MAPPING = "{\n" +
            "  \"properties\":{\n" +
            "    \"id\":{\"type\":\"keyword\",\"index\": false},\n" +
            "    \"app_code\":{\"type\":\"keyword\"},\n" +
            "    \"owner_code\":{\"type\":\"keyword\"},\n" +
            "    \"user_id\":{ \"type\":\"long\"},\n" +
            "    \"rank\":{\"type\":\"integer\"},\n" +
            "    \"user_flag\":{\"type\":\"integer\"},\n" +
            "    \"create_date\":{\"type\":\"date\",\"format\":\"yyyy-MM-dd HH:mm:ss\"},\n" +
            "    \"nick\":{\"type\":\"keyword\"},\n" +
            "    \"mobile\":{\"type\":\"keyword\"},\n" +
            "    \"invitecode\":{\"type\":\"keyword\"},\n" +
            "    \"amount\":{\"type\":\"double\"}\n" +
            "  } " +
            "}";
}
