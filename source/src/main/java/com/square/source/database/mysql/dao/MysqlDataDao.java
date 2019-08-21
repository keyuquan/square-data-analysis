package com.square.source.database.mysql.dao;

import com.alibaba.fastjson.JSONObject;
import com.square.source.conf.Constants;
import com.square.source.database.mysql.utils.DateUtils;
import com.square.source.database.mysql.utils.JDBCTools;
import com.square.source.domain.MysqlData;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;

import java.nio.charset.Charset;
import java.util.*;

public class MysqlDataDao {

    private static Map<String, String> lastDataMap = new HashMap<>();

    /**
     * 查询 MySQL 数据
     */
    public static List<Event> getMysqlData(Context flumeCtx, String stime, String etime, String eventCode) {

        if (StringUtils.isNotEmpty(stime)) {
            try {
                String sqlStr = flumeCtx.getString("mysql.data.sql");
                sqlStr = sqlStr.replace("${start_time}", stime).replace("${end_time}", etime);
                List<Map<String, Object>> dataList = JDBCTools.getDataQueryRunner(flumeCtx).query(sqlStr, new MapListHandler());
                List<Event> retryEventList = new ArrayList<>();
                for (Map<String, Object> thisLogReq : dataList) {
                    if (thisLogReq != null) {
                        Event thisEvent = EventBuilder.withBody(JSONObject.toJSONStringWithDateFormat(thisLogReq, "yyyy-MM-dd HH:mm:ss"), Charset.forName("UTF-8"));

                        if ("sk_user_baseinfo".equals(eventCode)) {
                            //  用户 允许更新数据 所以不能去重
                            retryEventList.add(thisEvent);
                        } else {
                            //  其他表 没有更新 ，所以要去重
                            if (lastDataMap.get(thisLogReq.get("unique_id").toString()) == null) {
                                retryEventList.add(thisEvent);
                            }
                        }
                    }
                }

                if (dataList.size() > 0) {
                    //  Map 保留最近 FLUME_DATA_SAVETIME 秒的数据
                    Set<Map.Entry<String, String>> set = lastDataMap.entrySet();
                    Iterator<Map.Entry<String, String>> iterator = set.iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, String> entry = iterator.next();
                        if (DateUtils.getSecondsByCompareL(entry.getValue(), etime) > Constants.FLUME_DATA_SAVETIME) {
                            iterator.remove();
                        }
                    }

                    //  开始时间和结束时间数据 有可能 重复，所以保存 结束时间的数据 ， 去重
                    for (Map<String, Object> thisLogReq : dataList) {
                        if (thisLogReq != null) {
                            String unique_id = thisLogReq.get("unique_id").toString();
                            String create_time = thisLogReq.get("create_time").toString().substring(0, 19);

                            //  保留最后 FLUME_DATA_SAVETIME 秒的数据 )
                            if ((!"sk_user_baseinfo".equals(eventCode)) && (DateUtils.getSecondsByCompareL(create_time, etime) <= Constants.FLUME_DATA_SAVETIME)) {
                                lastDataMap.put(unique_id, create_time);
                            }
                        }
                    }


                }


                return retryEventList;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * @param flumeCtx
     * @return
     * @throws Exception
     */
    public static String getMaxTableCreateTime(Context flumeCtx, String eventCode) throws Exception {
        if (StringUtils.isNotEmpty(eventCode)) {
            String sqlStr = "select   max(create_time) as maxCreateTime FROM " + eventCode + "  LIMIT 1";
            List<MysqlData> dataList = JDBCTools.getDataQueryRunner(flumeCtx).query(sqlStr, new BeanListHandler<>(MysqlData.class));
            if (dataList != null && dataList.size() > 0) {
                return dataList.get(0).getMaxCreateTime();
            }

        }
        return null;
    }

    /**
     * @param flumeCtx
     * @return
     * @throws Exception
     */
    public static String getMinTableCreateTime(Context flumeCtx, String eventCode) throws Exception {
        if (StringUtils.isNotEmpty(eventCode)) {
            String sqlStr = "select   min(create_time) as minCreateTime FROM " + eventCode + "  LIMIT 1";
            List<MysqlData> dataList = JDBCTools.getDataQueryRunner(flumeCtx).query(sqlStr, new BeanListHandler<>(MysqlData.class));
            if (dataList != null && dataList.size() > 0) {
                return dataList.get(0).getMinCreateTime();
            }

        }
        return null;
    }

}
