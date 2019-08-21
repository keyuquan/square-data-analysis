package com.square.source.conf;

import java.util.ResourceBundle;

/**
 * Created by admin on 2018/4/20.
 */
public class Config {

    static {
        //指定要读取的配置文件
        ResourceBundle bundle = ResourceBundle.getBundle("source-config");

        //获取配置文件里面内容
        System.out.println(bundle.getString("mysql.ibd.username"));
    }
    
    public static void main(String[] args) {
		
    	ResourceBundle bundle = ResourceBundle.getBundle("source-config");
        //获取配置文件里面内容
        System.out.println(bundle.getString("mysql.ibd.username"));
    	
	}

}
