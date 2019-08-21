package com.square.streams;

import java.util.List;

public class BinLog {

    /**
     * data : [{"id":"607276","user_id":"1166","billt_id":"24","money":"-48.0","money_before":"992560.46","money_after":"992512.46","intro":"牛牛抢包押金，红包最大赔率*红包金额，【1.00*48.00=48.00】","create_time":"2019-06-11 08:53:15","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"127964"},{"id":"607277","user_id":"1145","billt_id":"24","money":"-48.0","money_before":"995836.18","money_after":"995788.18","intro":"牛牛抢包押金，红包最大赔率*红包金额，【1.00*48.00=48.00】","create_time":"2019-06-11 08:53:16","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"127965"},{"id":"607278","user_id":"1216","billt_id":"24","money":"-270.0","money_before":"992262.05","money_after":"991992.05","intro":"牛牛发包押金，红包最大赔率*红包金额*红包个数，【1.00*90.00*(4-1)=270.00】","create_time":"2019-06-11 08:53:20","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"77814"},{"id":"607279","user_id":"1988","billt_id":"26","money":"3.70","money_before":"1222627.60","money_after":"1222631.30","intro":"牛牛结算，手续费【3.70】","create_time":"2019-06-11 08:53:20","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"77808"},{"id":"607280","user_id":"1200","billt_id":"25","money":"148.0","money_before":"991080.95","money_after":"991228.95","intro":"牛牛结算，您的点数【9】，闲家家点数【4】，押金【74.00】，赔付【74.00】，手续费【-3.70】","create_time":"2019-06-11 08:53:20","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"77808"},{"id":"607281","user_id":"1160","billt_id":"25","money":"0.0","money_before":"989787.57","money_after":"989787.57","intro":"牛牛结算，您的点数【4】，庄家点数【9】，押金【74.00】，赔付【-74.00】","create_time":"2019-06-11 08:53:20","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"127951"},{"id":"607282","user_id":"1200","billt_id":"40","money":"-3.70","money_before":"991228.95","money_after":"991225.25","intro":"牛牛结算，手续费【-3.70】","create_time":"2019-06-11 08:53:20","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"77808"},{"id":"607284","user_id":"1092","billt_id":"24","money":"-90.0","money_before":"995645.77","money_after":"995555.77","intro":"牛牛抢包押金，红包最大赔率*红包金额，【1.00*90.00=90.00】","create_time":"2019-06-11 08:53:21","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"127967"},{"id":"607285","user_id":"1158","billt_id":"24","money":"-90.0","money_before":"992233.41","money_after":"992143.41","intro":"牛牛抢包押金，红包最大赔率*红包金额，【1.00*90.00=90.00】","create_time":"2019-06-11 08:53:23","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"127968"},{"id":"607286","user_id":"1157","billt_id":"24","money":"-90.0","money_before":"995586.10","money_after":"995496.10","intro":"牛牛抢包押金，红包最大赔率*红包金额，【1.00*90.00=90.00】","create_time":"2019-06-11 08:53:25","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"127969"},{"id":"607287","user_id":"1090","billt_id":"25","money":"84.0","money_before":"995320.94","money_after":"995404.94","intro":"牛牛结算，您的点数【5】，庄家点数【5】，押金【42.00】，赔付【42.00】，手续费【-2.10】","create_time":"2019-06-11 08:53:29","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"127955"},{"id":"607288","user_id":"1988","billt_id":"26","money":"2.10","money_before":"1222631.30","money_after":"1222633.40","intro":"牛牛结算，手续费【2.10】","create_time":"2019-06-11 08:53:29","status":"0","del_flag":"0","commission_flag":"1","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"77810"},{"id":"607289","user_id":"1156","billt_id":"25","money":"0.0","money_before":"994652.86","money_after":"994652.86","intro":"牛牛结算，您的点数【4】，庄家点数【5】，押金【42.00】，赔付【-42.00】","create_time":"2019-06-11 08:53:29","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"127953"},{"id":"607290","user_id":"1090","billt_id":"40","money":"-2.10","money_before":"995404.94","money_after":"995402.84","intro":"牛牛结算，手续费【-2.10】","create_time":"2019-06-11 08:53:29","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"77810"},{"id":"607291","user_id":"1988","billt_id":"26","money":"2.10","money_before":"1222633.40","money_after":"1222635.50","intro":"牛牛结算，手续费【2.10】","create_time":"2019-06-11 08:53:30","status":"0","del_flag":"0","commission_flag":"1","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"77810"},{"id":"607292","user_id":"1155","billt_id":"25","money":"84.0","money_before":"992853.43","money_after":"992937.43","intro":"牛牛结算，您的点数【8】，庄家点数【5】，押金【42.00】，赔付【42.00】，手续费【-2.10】","create_time":"2019-06-11 08:53:30","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"127957"},{"id":"607293","user_id":"1145","billt_id":"25","money":"0.0","money_before":"995788.18","money_after":"995788.18","intro":"牛牛结算，您的点数【2】，庄家点数【5】，押金【42.00】，赔付【-42.00】","create_time":"2019-06-11 08:53:30","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"127954"},{"id":"607294","user_id":"1155","billt_id":"40","money":"-2.10","money_before":"992937.43","money_after":"992935.33","intro":"牛牛结算，手续费【-2.10】","create_time":"2019-06-11 08:53:30","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"77810"},{"id":"607295","user_id":"1988","billt_id":"26","money":"2.10","money_before":"1222635.50","money_after":"1222637.60","intro":"牛牛结算，手续费【2.10】","create_time":"2019-06-11 08:53:30","status":"0","del_flag":"0","commission_flag":"1","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"77810"},{"id":"607296","user_id":"1159","billt_id":"25","money":"84.0","money_before":"996401.83","money_after":"996485.83","intro":"牛牛结算，您的点数【8】，庄家点数【5】，押金【42.00】，赔付【42.00】，手续费【-2.10】","create_time":"2019-06-11 08:53:30","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"127956"},{"id":"607297","user_id":"1159","billt_id":"40","money":"-2.10","money_before":"996485.83","money_after":"996483.73","intro":"牛牛结算，手续费【-2.10】","create_time":"2019-06-11 08:53:30","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"77810"},{"id":"607298","user_id":"1988","billt_id":"26","money":"2.10","money_before":"1222637.60","money_after":"1222639.70","intro":"牛牛结算，手续费【2.10】","create_time":"2019-06-11 08:53:30","status":"0","del_flag":"0","commission_flag":"1","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"77810"},{"id":"607299","user_id":"1092","billt_id":"25","money":"84.0","money_before":"995555.77","money_after":"995639.77","intro":"牛牛结算，您的点数【6】，庄家点数【5】，押金【42.00】，赔付【42.00】，手续费【-2.10】","create_time":"2019-06-11 08:53:30","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"127958"},{"id":"607300","user_id":"1092","billt_id":"40","money":"-2.10","money_before":"995639.77","money_after":"995637.67","intro":"牛牛结算，手续费【-2.10】","create_time":"2019-06-11 08:53:30","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"77810"},{"id":"607301","user_id":"1988","billt_id":"26","money":"4.20","money_before":"1222639.70","money_after":"1222643.90","intro":"牛牛结算，手续费【4.20】","create_time":"2019-06-11 08:53:30","status":"0","del_flag":"0","commission_flag":"1","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"77810"},{"id":"607302","user_id":"1218","billt_id":"25","money":"168.0","money_before":"991070.50","money_after":"991238.50","intro":"牛牛结算，您的点数【5】，闲家家点数【4,2,5,8,8,6】，押金【252.00】，赔付【-84.00】，手续费【-4.20】","create_time":"2019-06-11 08:53:30","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"77810"},{"id":"607303","user_id":"1218","billt_id":"40","money":"-4.20","money_before":"991238.50","money_after":"991234.30","intro":"牛牛结算，手续费【-4.20】","create_time":"2019-06-11 08:53:30","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"77810"},{"id":"607304","user_id":"1225","billt_id":"24","money":"-408.0","money_before":"985952.35","money_after":"985544.35","intro":"牛牛发包押金，红包最大赔率*红包金额*红包个数，【1.00*68.00*(7-1)=408.00】","create_time":"2019-06-11 08:53:30","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"77816"},{"id":"607305","user_id":"1","billt_id":"31","money":"0.0","money_before":"999541.90","money_after":"999541.90","intro":"群主牛牛红包抽水2.10元，获得返佣0.00元","create_time":"2019-06-11 08:53:30","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"607288"},{"id":"607306","user_id":"1988","billt_id":"33","money":"0.0","money_before":"1222643.90","money_after":"1222643.90","intro":"群主牛牛红包抽水2.10元，发放返佣0.00元","create_time":"2019-06-11 08:53:30","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"607288"},{"id":"607308","user_id":"1","billt_id":"31","money":"0.0","money_before":"999541.90","money_after":"999541.90","intro":"群主牛牛红包抽水2.10元，获得返佣0.00元","create_time":"2019-06-11 08:53:31","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"607291"},{"id":"607309","user_id":"1988","billt_id":"33","money":"0.0","money_before":"1222643.90","money_after":"1222643.90","intro":"群主牛牛红包抽水2.10元，发放返佣0.00元","create_time":"2019-06-11 08:53:31","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"607291"},{"id":"607310","user_id":"1","billt_id":"31","money":"0.0","money_before":"999541.90","money_after":"999541.90","intro":"群主牛牛红包抽水2.10元，获得返佣0.00元","create_time":"2019-06-11 08:53:31","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"607295"},{"id":"607311","user_id":"1988","billt_id":"33","money":"0.0","money_before":"1222643.90","money_after":"1222643.90","intro":"群主牛牛红包抽水2.10元，发放返佣0.00元","create_time":"2019-06-11 08:53:31","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"607295"},{"id":"607312","user_id":"1","billt_id":"31","money":"0.0","money_before":"999541.90","money_after":"999541.90","intro":"群主牛牛红包抽水2.10元，获得返佣0.00元","create_time":"2019-06-11 08:53:31","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"607298"},{"id":"607313","user_id":"1988","billt_id":"33","money":"0.0","money_before":"1222643.90","money_after":"1222643.90","intro":"群主牛牛红包抽水2.10元，发放返佣0.00元","create_time":"2019-06-11 08:53:31","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"607298"},{"id":"607314","user_id":"1","billt_id":"31","money":"0.0","money_before":"999541.90","money_after":"999541.90","intro":"群主牛牛红包抽水4.20元，获得返佣0.00元","create_time":"2019-06-11 08:53:31","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"607301"},{"id":"607315","user_id":"1988","billt_id":"33","money":"0.0","money_before":"1222643.90","money_after":"1222643.90","intro":"群主牛牛红包抽水4.20元，发放返佣0.00元","create_time":"2019-06-11 08:53:31","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"607301"},{"id":"607316","user_id":"1159","billt_id":"24","money":"-68.0","money_before":"996483.73","money_after":"996415.73","intro":"牛牛抢包押金，红包最大赔率*红包金额，【1.00*68.00=68.00】","create_time":"2019-06-11 08:53:32","status":"0","del_flag":"0","commission_flag":"0","create_by":null,"last_update_time":null,"last_update_by":null,"biz_id":"127971"}]
     * database : pig
     * es : 1561872817000
     * id : 5398
     * isDdl : false
     * mysqlType : {"id":"bigint(20) unsigned","user_id":"int(11) unsigned","billt_id":"int(11) unsigned","money":"decimal(20,2)","money_before":"decimal(20,2)","money_after":"decimal(20,2)","intro":"varchar(256)","create_time":"datetime","status":"tinyint(4) unsigned","del_flag":"tinyint(1)","commission_flag":"tinyint(4)","create_by":"bigint(20)","last_update_time":"datetime","last_update_by":"bigint(20)","biz_id":"bigint(20)"}
     * old : null
     * pkNames : ["id"]
     * sql :
     * sqlType : {"id":-5,"user_id":4,"billt_id":4,"money":3,"money_before":3,"money_after":3,"intro":12,"create_time":93,"status":-6,"del_flag":-7,"commission_flag":-6,"create_by":-5,"last_update_time":93,"last_update_by":-5,"biz_id":-5}
     * table : sk_bill
     * ts : 1561887602605
     * type : INSERT
     */
    private String database;
    private String es;
    private String id;
    private String isDdl;
    private Object mysqlType;
    private Object old;
    private String sql;
    private Object sqlType;
    private String table;
    private String ts;
    private String type;
    private List<Object> data;
    private List<String> pkNames;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getEs() {
        return es;
    }

    public void setEs(String es) {
        this.es = es;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String isIsDdl() {
        return isDdl;
    }

    public void setIsDdl(String isDdl) {
        this.isDdl = isDdl;
    }

    public Object getMysqlType() {
        return mysqlType;
    }

    public void setMysqlType(Object mysqlType) {
        this.mysqlType = mysqlType;
    }

    public Object getOld() {
        return old;
    }

    public void setOld(Object old) {
        this.old = old;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object getSqlType() {
        return sqlType;
    }

    public void setSqlType(Object sqlType) {
        this.sqlType = sqlType;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public List<String> getPkNames() {
        return pkNames;
    }

    public void setPkNames(List<String> pkNames) {
        this.pkNames = pkNames;
    }

}
