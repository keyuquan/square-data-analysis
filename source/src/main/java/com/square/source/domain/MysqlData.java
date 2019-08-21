package com.square.source.domain;

import java.io.Serializable;

public class MysqlData implements Serializable {
    /**
     * 最大创建时间
     */
    private String maxCreateTime;

    /**
     * 最小创建时间
     */
    private String minCreateTime;


    public String getMaxCreateTime() {
        return maxCreateTime;
    }

    public void setMaxCreateTime(String maxCreateTime) {
        this.maxCreateTime = maxCreateTime;
    }

    public String getMinCreateTime() {
        return minCreateTime;
    }

    public void setMinCreateTime(String minCreateTime) {
        this.minCreateTime = minCreateTime;
    }

}