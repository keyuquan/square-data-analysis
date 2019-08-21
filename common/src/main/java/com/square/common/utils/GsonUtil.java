package com.square.common.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.mortbay.log.Log;

/**
 * Created by admin on 2018/4/20.
 */
public class GsonUtil {
    // 将Json数据解析成相应的映射对象
    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        T result =null;
        try {
            Gson gson = new Gson();
            result = gson.fromJson(jsonData, type);
        } catch (JsonSyntaxException e) {
            Log.info("json解析失败");
        }
        return result;
    }

    public static void main(String[] args) {
        parseJsonWithGson("dasd",String.class);
    }

}
