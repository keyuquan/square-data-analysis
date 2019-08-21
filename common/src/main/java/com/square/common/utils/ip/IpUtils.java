package com.square.common.utils.ip;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

/**
 * IP地址获取。
 * ip库来源于网站https://www.ipip.net/product/client.html，库名称：ipiptest.ipdb
 * 建议关注该网站的更新，每半年更换一次最新的。
 */
public class IpUtils {
    private static Reader db = null;
    private final static String ipFileName = "/ipiptest.ipdb";
    private static Logger logger = LoggerFactory.getLogger(IpUtils.class); // 日志记录

    static {
        File file = new File(IpUtils.class.getResource(ipFileName).getPath());
        try {
            db = new Reader(file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Init ip db error.", e);
        }
    }

    public static String[] getIpAddress(String ipString) {
        String[] address = new String[]{"", "", ""};
        if (StringUtils.isEmpty(ipString)) {
            return address;
        }
        try {
            String[] result = db.find(ipString, "CN");
            if (result.length >= 1) {
                address[0] = result[0];
            }
            if (result.length >= 2) {
                address[1] = result[1];
            }
            if (result.length >= 3) {
                address[2] = result[2];
            }
        } catch (Exception e) {
            logger.error(String.format("Get ip:{%s} address error:", ipString), e);
        }
        return address;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(IpUtils.getIpAddress("127.0.0.1")));
        System.out.println(Arrays.toString(IpUtils.getIpAddress("")));
        System.out.println(Arrays.toString(IpUtils.getIpAddress(null)));
        System.out.println(Arrays.toString(IpUtils.getIpAddress("111.172.60.178")));
        System.out.println(Arrays.toString(IpUtils.getIpAddress("169.235.24.133")));
    }
}
