package com.square.source.domain;

import java.io.Serializable;
import java.util.Date;

public class IbdTransterData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;


    /**
     * 　app  code
     */
    private String AppCode;

    /**
     * 业主标识
     */
    private String OwnerCode;


    /**
     * 业务类型（注册，行为，商业...）
     */
    private String EventCode;

    /**
     * 数据来源（mysql, es, mongo）
     */
    private String DataSource;

    /**
     * 查询条件（开始时间）
     */
    private String conditionStime;

    /**
     * 查询条件（结束时间）
     */
    private String conditionEtime;

    /**
     * 导入的记录数
     */
    private Long dataCount;

    /**
     * 状态（0:进入任务，1:导入成功，-1:导入失败）
     */
    private Integer status;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 重试次数 , 默认值为0
     */
    private Integer retry;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String updateTime;

    public IbdTransterData() {
    }

    public IbdTransterData(String appCode, String ownerCode, String eventCode, String dataSource) {
        this.AppCode = appCode;
        this.OwnerCode = ownerCode;
        this.EventCode = eventCode;
        this.DataSource = dataSource;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppCode() {
        return AppCode;
    }

    public void setAppCode(String appCode) {
        AppCode = appCode;
    }

    public String getOwnerCode() {
        return OwnerCode;
    }

    public void setOwnerCode(String ownerCode) {
        OwnerCode = ownerCode;
    }

    public String getEventCode() {
        return EventCode;
    }

    public void setEventCode(String eventCode) {
        EventCode = eventCode;
    }

    public String getDataSource() {
        return DataSource;
    }

    public void setDataSource(String dataSource) {
        DataSource = dataSource;
    }

    public String getConditionStime() {
        return conditionStime;
    }

    public void setConditionStime(String conditionStime) {
        this.conditionStime = conditionStime;
    }

    public String getConditionEtime() {
        return conditionEtime;
    }

    public void setConditionEtime(String conditionEtime) {
        this.conditionEtime = conditionEtime;
    }

    public Long getDataCount() {
        return dataCount;
    }

    public void setDataCount(Long dataCount) {
        this.dataCount = dataCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getRetry() {
        return retry;
    }

    public void setRetry(Integer retry) {
        this.retry = retry;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}