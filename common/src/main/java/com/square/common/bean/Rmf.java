package com.square.common.bean;

import java.io.Serializable;

/**
 * Created by chenxiaogang on 18/5/18 0018.
 * version: 0.1
 */
public class Rmf implements Serializable{

    private Double avgPayTimes;

    private Double avgNoConsumerDays;

    private Double aggConsumptionTotal;

    public Double getAvgPayTimes() {
        return avgPayTimes;
    }

    public void setAvgPayTimes(Double avgPayTimes) {
        this.avgPayTimes = avgPayTimes;
    }

    public Double getAvgNoConsumerDays() {
        return avgNoConsumerDays;
    }

    public void setAvgNoConsumerDays(Double avgNoConsumerDays) {
        this.avgNoConsumerDays = avgNoConsumerDays;
    }

    public Double getAggConsumptionTotal() {
        return aggConsumptionTotal;
    }

    public void setAggConsumptionTotal(Double aggConsumptionTotal) {
        this.aggConsumptionTotal = aggConsumptionTotal;
    }
}
