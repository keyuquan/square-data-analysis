package com.square.common.bean;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenxiaogang on 18/10/10 0025.
 * version: 0.1
 * 路由表bean
 */
public class EdaIndexRoute implements Serializable{


    public static final String APP_CODE_FIELD = "app_code";
    public static final String EVENT_CODE_FIELD = "event_code";
    public static final String INDEX_NAME_FIELD = "index_name";
    public static final String START_TIME_FIELD = "start_time";
    public static final String END_TIME_FIELD = "end_time";
    public static final String SIZE_FIELD = "size";

    private String id;

    private String appCode;

    private String eventCode;

    private String indexName;

    private String startTime;

    private String endTime;

    private long size;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Map<String,Object> convertToMap(){
        Map<String,Object> map = new HashMap<>();
        map.put(EdaIndexRoute.APP_CODE_FIELD,this.appCode);
        map.put(EdaIndexRoute.EVENT_CODE_FIELD,this.eventCode);
        map.put(EdaIndexRoute.INDEX_NAME_FIELD,this.indexName);
        map.put(EdaIndexRoute.START_TIME_FIELD,this.startTime);
        if (StringUtils.isNoneBlank(endTime)) {
            map.put(EdaIndexRoute.END_TIME_FIELD, this.endTime);
        }
        map.put(EdaIndexRoute.SIZE_FIELD,this.size);
        return map;
    }


}
