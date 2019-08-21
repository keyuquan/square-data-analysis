package com.square.jobs.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    static  Pattern  pattern = Pattern.compile("\\d+");

    //截取数字
    public static String getNumbers(String content) {
        Matcher matcher = pattern.matcher(content.replace(",",""));
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "0";
    }


}
