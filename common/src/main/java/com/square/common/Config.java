package com.square.common;

import java.util.ResourceBundle;

/**
 * Created by admin on 2018/10/08.
 */
public class Config {

    //kafka的连接地址
    public static String BROKER_LISTS;
    //es  的连接地址
    public static String ES_HOSTS;

    static {
        //指定要读取的配置文件
        ResourceBundle bundle = ResourceBundle.getBundle("eda-common-config");
        //获取配置文件里面内容
        BROKER_LISTS = bundle.getString("bootstrap_servers").trim();
        ES_HOSTS = bundle.getString("es_hosts").trim();

    }

}
