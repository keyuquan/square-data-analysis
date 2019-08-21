package com.square.source.database.mysql.dao;

import com.square.source.database.mysql.utils.JDBCTools;
import com.square.source.domain.IbdTransterData;
import com.square.source.enums.DataStatusEnum;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class IbdTransterDataDao {

    private static QueryRunner queryRunner = JDBCTools.getIbdQueryRunner();
    private static DateFormat timeDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取导数据记录中，“最晚的”导入时间（断点续传）
     *
     * @param ibdTransterData
     * @return
     * @throws Exception
     */
    public static String getLatestTransferTime(IbdTransterData ibdTransterData) throws Exception {
        if (ibdTransterData != null && StringUtils.isNotEmpty(ibdTransterData.getAppCode())) {
            String sqlStr = "SELECT condition_etime as conditionEtime FROM ibd_transter_data WHERE status =1 and  app_code = '" + ibdTransterData.getAppCode() + "' ";
            if (StringUtils.isNotEmpty(ibdTransterData.getOwnerCode())) {
                sqlStr += "AND owner_code = '" + ibdTransterData.getOwnerCode() + "' ";
            }
            if (StringUtils.isNotEmpty(ibdTransterData.getEventCode())) {
                sqlStr += "AND event_code = '" + ibdTransterData.getEventCode() + "' ";
            }
            if (StringUtils.isNotEmpty(ibdTransterData.getDataSource())) {
                sqlStr += "AND data_source = '" + ibdTransterData.getDataSource() + "' ";
            }
            sqlStr += "ORDER BY condition_etime DESC LIMIT 1 ";
            List<IbdTransterData> dataList = queryRunner.query(sqlStr, new BeanListHandler<>(IbdTransterData.class));
            if (dataList != null && dataList.size() > 0) {
                return dataList.get(0).getConditionEtime();
            }
        }
        return null;
    }

    /**
     * 删除数据
     *
     * @param ibdTransterData
     * @return
     * @throws Exception
     */
    public static String deleteTransfer(IbdTransterData ibdTransterData) throws Exception {
        if (ibdTransterData != null && StringUtils.isNotEmpty(ibdTransterData.getAppCode())) {
            String sqlStr = "delete FROM ibd_transter_data WHERE app_code = '" + ibdTransterData.getAppCode() + "' ";
            if (StringUtils.isNotEmpty(ibdTransterData.getOwnerCode())) {
                sqlStr += "AND owner_code = '" + ibdTransterData.getOwnerCode() + "' ";
            }
            if (StringUtils.isNotEmpty(ibdTransterData.getEventCode())) {
                sqlStr += "AND event_code = '" + ibdTransterData.getEventCode() + "' ";
            }
            if (StringUtils.isNotEmpty(ibdTransterData.getDataSource())) {
                sqlStr += "AND data_source = '" + ibdTransterData.getDataSource() + "' ";
            }

            queryRunner.update(sqlStr);
        }
        return null;
    }

    /**
     * 新增和更新记录
     *
     * @param ibdTransterData
     * @return
     */
    public static String addIbdTransterData(IbdTransterData ibdTransterData, boolean isCur, Long curCount) {

        try {


            // 添加纪录
            String sql = "insert into ibd_transter_data (id,app_code, owner_code, event_code, data_source, condition_stime, condition_etime, data_count, status, remark, retry) values(?,?,?,?,?,?,?,?,?,?,?)";
            List<Object> paramList = new ArrayList<Object>();
            String thisId = UUID.randomUUID().toString().replace("-", "");
            paramList.add(thisId);
            paramList.add(ibdTransterData.getAppCode());
            paramList.add(ibdTransterData.getOwnerCode());
            paramList.add(ibdTransterData.getEventCode());
            paramList.add(ibdTransterData.getDataSource());
            paramList.add(ibdTransterData.getConditionStime());
            paramList.add(ibdTransterData.getConditionEtime());
            paramList.add(ibdTransterData.getDataCount());
            paramList.add(DataStatusEnum.INIT.getCode());
            paramList.add(ibdTransterData.getRemark());
            paramList.add(0);
            queryRunner.update(sql, paramList.toArray());
            if (curCount % 24 == 0) {
                if (isCur) {
                    //  保留 1 分钟数据
                    queryRunner.update("delete from   ibd_transter_data where status =1 and app_code='" + ibdTransterData.getAppCode() + "' and owner_code ='" + ibdTransterData.getOwnerCode() + "'and  event_code='" + ibdTransterData.getEventCode() + "'and  data_source='" + ibdTransterData.getDataSource() + "'and condition_stime<='" + timeDf.format(timeDf.parse(ibdTransterData.getConditionEtime()).getTime() - 24 * 1000l) + "'");
                } else {
                    // 保留 24 小时 数据
                    queryRunner.update("delete from   ibd_transter_data where status =1 and app_code='" + ibdTransterData.getAppCode() + "' and owner_code ='" + ibdTransterData.getOwnerCode() + "'and  event_code='" + ibdTransterData.getEventCode() + "'and  data_source='" + ibdTransterData.getDataSource() + "'and condition_stime<='" + timeDf.format(timeDf.parse(ibdTransterData.getConditionEtime()).getTime() - 24 * 60 * 60 * 1000l) + "'");

                }
            }

            return thisId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 新增和更新记录
     *
     * @param ibdTransterData
     * @return
     */
    public static String deleteIbdTransterData(IbdTransterData ibdTransterData, long time) {
        try {
            // 只保留 1小时  正确传输的数据
            queryRunner.update("delete from   ibd_transter_data where status =1 and app_code='" + ibdTransterData.getAppCode() + "' and owner_code ='" + ibdTransterData.getOwnerCode() + "'and  event_code='" + ibdTransterData.getEventCode() + "'and condition_stime<='" + timeDf.format(timeDf.parse(ibdTransterData.getConditionEtime()).getTime() - time) + "'");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 修改记录
     * 主要是修改： data_count , status , remark , update_time 这几个字段
     *
     * @param ibdTransterData
     */
    public static void updateIbdTransterDataById(IbdTransterData ibdTransterData) {
        if (StringUtils.isNotEmpty(ibdTransterData.getId())) {
            try {
                String sql = "UPDATE ibd_transter_data SET ";
                if (ibdTransterData.getDataCount() != null) {
                    sql += " data_count = " + ibdTransterData.getDataCount() + ", ";
                }
                if (ibdTransterData.getStatus() != null) {
                    sql += " status = " + ibdTransterData.getStatus() + ", ";
                }
                if (StringUtils.isNotEmpty(ibdTransterData.getRemark())) {
                    sql += " remark = '" + ibdTransterData.getRemark() + "', ";
                }
                if (ibdTransterData.getRetry() != null && ibdTransterData.getRetry() > 0) {
                    sql += " retry = " + ibdTransterData.getRetry() + ", ";
                }
                // 添加修改时间，和id的查询条件
                sql += " update_time = '" + timeDf.format(new Date()) + "' WHERE id = '" + ibdTransterData.getId() + "'";
                queryRunner.update(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据业务类型查询, 获取需要重试（最多3次）的错误记录数据
     *
     * @param stime
     * @param etime
     * @return
     */
    public static List<IbdTransterData> getExceptionRetryDataByEventCode(String appCode, String ownerCode, String eventCode, String stime, String etime) {

        try {
            String showCols = "id, retry, app_code AS 'appCode', owner_code AS 'ownerCode', event_code AS 'eventCode',data_source AS 'dataSource',  condition_stime AS 'conditionStime', condition_etime AS 'conditionEtime', status AS 'status', create_time AS 'createTime'";
            String sql = "SELECT " + showCols + " FROM ibd_transter_data WHERE status <= 0 AND retry < 3 ";
            if (StringUtils.isNotEmpty(appCode)) {
                sql += "AND app_code = '" + appCode + "' ";
            }
            if (StringUtils.isNotEmpty(appCode)) {
                sql += "AND owner_code = '" + ownerCode + "' ";
            }
            if (StringUtils.isNotEmpty(eventCode)) {
                sql += "AND event_code = '" + eventCode + "' ";
            }
            if (StringUtils.isNotEmpty(stime)) {
                sql += "AND create_time >= '" + stime + "' ";
            }
            if (StringUtils.isNotEmpty(etime)) {
                sql += "AND create_time <= '" + etime + "' ";
            }
            List<IbdTransterData> dataList = queryRunner.query(sql, new BeanListHandler<>(IbdTransterData.class));
            if (dataList != null && dataList.size() > 0) {
                return dataList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
