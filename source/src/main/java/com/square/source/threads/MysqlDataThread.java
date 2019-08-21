package com.square.source.threads;

import com.alibaba.fastjson.JSONObject;
import com.square.source.conf.Constants;
import com.square.source.database.mysql.dao.IbdTransterDataDao;
import com.square.source.database.mysql.dao.MysqlDataDao;
import com.square.source.database.mysql.utils.DateUtils;
import com.square.source.domain.IbdTransterData;
import com.square.source.enums.DataStatusEnum;
import com.square.source.enums.DatabaseTypeEnum;
import com.square.source.enums.TransferStatusEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class MysqlDataThread extends AbstractSource implements Configurable, EventDrivenSource {

    private static final String CONFIG_TRANSFER_STATUS = "transferStatus";
    private static final String CONFIG_START_TIME = "startTime";
    private static final String CONFIG_END_TIME = "endTime";
    private static final String CONFIG_THREAD_INTERAL_SEC = "threadInteralSec";
    private static final String CONFIG_QUERY_INTERAL_SEC = "queryInteralSec";
    private static final String CONFIG_APPCODE = "appCode";
    private static final String CONFIG_OWNERCODE = "ownerCode";
    private static final String CONFIG_EVENTCODE = "eventCode";

    // error重试机制，init时间为 30min
    private static final Integer errorRetryInitSec = 30 * 60;

    // error重试机制，时间间隔为 1 min
    private static final Integer errorRetryInteralSec = 1 * 60;

    // flume框架，配置文件
    private Context flumeContext;

    //读取本地的配置文件
    private static ResourceBundle configBundle = ResourceBundle.getBundle("source-config");

    // 启动的多线程数
    private Integer threadCount = 1;

    // 同一线程，两次运行的间隔时间（秒数）
    private Integer threadInteralSec = null;

    // 每次查询多长时间间隔内的数据（秒数）
    private Integer queryInteralSec = null;

    // 导数据的方式状态（normal-正常 ; renew-重传）
    private String transferStatus = null;

    // 导数据的开始时间 "yyyy-MM-dd HH:mm:ss" 格式
    private String startTime = null;

    // 导数据的结束时间 "yyyy-MM-dd HH:mm:ss" 格式
    private String endTime = null;

    // 导数据的开始时间（毫秒数）
    private Long startTimeMillis = null;

    // 导数据的结束时间（毫秒数）
    private Long endTimeMillis = null;

    // 线程池（导数据）
    private ScheduledExecutorService scheduledExecutorService;

    // 线程池（错误重试机制）
    private ScheduledExecutorService errorRetryExecutorService;

    // 发送数据 flume-channel
    private ChannelProcessor channelProcessor;

    // app 标识
    private String appCode = null;

    // 业主标识
    private String ownerCode = null;

    // 数据类型标识
    private String eventCode = null;


    // 循环计数器（多线程共用）
    private AtomicLong beginGolab = new AtomicLong(0);

    private AtomicLong count = new AtomicLong(0);

    final Logger LOGGER = LoggerFactory.getLogger(MysqlDataThread.class);


    private static Map<String, Integer> lastDataMap = new HashMap<>();


    /**
     * 加载flume的mongodb数据源配置信息
     */
    @Override
    public void configure(Context context) {
        try {
            flumeContext = context;

            if (flumeContext != null && StringUtils.isNotEmpty(flumeContext.getString(CONFIG_TRANSFER_STATUS))) {
                transferStatus = flumeContext.getString(CONFIG_TRANSFER_STATUS);
            } else if (configBundle.containsKey(CONFIG_TRANSFER_STATUS)) {
                transferStatus = configBundle.getString(CONFIG_TRANSFER_STATUS);

            }

            if (flumeContext != null && StringUtils.isNotEmpty(flumeContext.getString(CONFIG_APPCODE))) {
                appCode = flumeContext.getString(CONFIG_APPCODE);
            } else if (configBundle.containsKey(CONFIG_APPCODE)) {
                appCode = configBundle.getString(CONFIG_APPCODE);

            }

            if (flumeContext != null && StringUtils.isNotEmpty(flumeContext.getString(CONFIG_OWNERCODE))) {
                ownerCode = flumeContext.getString(CONFIG_OWNERCODE);
            } else if (configBundle.containsKey(CONFIG_OWNERCODE)) {
                ownerCode = configBundle.getString(CONFIG_OWNERCODE);

            }

            if (flumeContext != null && StringUtils.isNotEmpty(flumeContext.getString(CONFIG_EVENTCODE))) {
                eventCode = flumeContext.getString(CONFIG_EVENTCODE);
            } else if (configBundle.containsKey(CONFIG_EVENTCODE)) {
                eventCode = configBundle.getString(CONFIG_EVENTCODE);

            }

            if (flumeContext != null && StringUtils.isNotEmpty(flumeContext.getString(CONFIG_START_TIME))) {
                startTime = flumeContext.getString(CONFIG_START_TIME);

                String createMin = MysqlDataDao.getMinTableCreateTime(flumeContext, eventCode);

                //  最小数据时间大于 配置的 开始时间 ，使用最小数据时间，避免时间浪费
                if (DateUtils.compareDateTime(startTime, createMin) > 0) {
                    startTime = createMin;
                }

            } else if (configBundle.containsKey(CONFIG_START_TIME)) {
                startTime = configBundle.getString(CONFIG_START_TIME);

            }
            if (flumeContext != null && StringUtils.isNotEmpty(flumeContext.getString(CONFIG_END_TIME))) {
                endTime = flumeContext.getString(CONFIG_END_TIME);
            } else if (configBundle.containsKey(CONFIG_END_TIME)) {
                endTime = configBundle.getString(CONFIG_END_TIME);

            }

            if (flumeContext != null && StringUtils.isNotEmpty(flumeContext.getString(CONFIG_THREAD_INTERAL_SEC))) {
                threadInteralSec = flumeContext.getInteger(CONFIG_THREAD_INTERAL_SEC);
            } else if (configBundle.containsKey(CONFIG_THREAD_INTERAL_SEC)) {
                threadInteralSec = Integer.parseInt(configBundle.getString(CONFIG_THREAD_INTERAL_SEC));

            }
            if (flumeContext != null && StringUtils.isNotEmpty(flumeContext.getString(CONFIG_QUERY_INTERAL_SEC))) {
                queryInteralSec = flumeContext.getInteger(CONFIG_QUERY_INTERAL_SEC);
            } else if (configBundle.containsKey(CONFIG_QUERY_INTERAL_SEC)) {
                queryInteralSec = Integer.parseInt(configBundle.getString(CONFIG_QUERY_INTERAL_SEC));
            }

            DateFormat timeDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (StringUtils.isNotEmpty(startTime)) {
                startTimeMillis = timeDf.parse(startTime).getTime();
                beginGolab.set(startTimeMillis);
            }
            if (StringUtils.isNotEmpty(endTime)) {
                endTimeMillis = timeDf.parse(endTime).getTime();
            }

            LOGGER.debug("appCode : " + appCode + "ownerCode : " + ownerCode + "eventCode : " + eventCode);


            // 如果不是 renew 状态，则从mysql的导入数据记录中找到上次最晚的传输时间
            if (!TransferStatusEnum.RENEW.getCode().equals(transferStatus)) {
                // 查询数据库，找出导数据记录表中，最晚的一条记录
                IbdTransterData ibdTransterData = new IbdTransterData(appCode, ownerCode, eventCode, DatabaseTypeEnum.MYSQL.getCode());
                String lastTransferTime = IbdTransterDataDao.getLatestTransferTime(ibdTransterData);
                if (StringUtils.isNotEmpty(lastTransferTime)) {
                    startTime = lastTransferTime;
                    startTimeMillis = timeDf.parse(startTime).getTime();
                    beginGolab.set(startTimeMillis);
                }
            } else {
                // 重新开始就删除所有数据
                IbdTransterData ibdTransterData = new IbdTransterData(appCode, ownerCode, eventCode, DatabaseTypeEnum.MYSQL.getCode());
                IbdTransterDataDao.deleteTransfer(ibdTransterData);
            }
            LOGGER.error("@@@ startTime : " + startTime);
            LOGGER.error("@@@ startTimeMillis : " + startTimeMillis);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("加载配置文件失败 : " + e.getMessage());
        }
    }


    /**
     * Starts the source. Starts the metrics counter.
     */
    @Override
    public void start() {
        channelProcessor = this.getChannelProcessor();
        scheduledExecutorService = Executors.newScheduledThreadPool(threadCount);
        // 循环启动多线程
        for (int k = 0; k < threadCount; k++) {
            try {
                // 不同线程中间休息 2 秒
                Thread.sleep(2000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {

                    try {
                        // 获取开始时间
                        Long begin = beginGolab.get();
                        //模式一： 默认　　queryInteralSec　轮循机制
                        Long end = begin + queryInteralSec * 1000l;
                        boolean isCur = false;

                        // 模式二：threadInteralSec　轮循机制
                        Long currentTimeMillis = (System.currentTimeMillis() / 1000) * 1000l - 2000l;
                        if (currentTimeMillis < end) {
                            // 当轮循到 最新的时候 ，end 使用 最新时间
                            end = currentTimeMillis;
                            // 没有到轮循时间　
                            if (currentTimeMillis < begin + threadInteralSec * 1000l) {
                                return;
                            }
                            isCur = true;
                        }

                        //模式三：数据轮循机制
                        // 获取数据的最大创建时间
                        String createMaxTable = MysqlDataDao.getMaxTableCreateTime(flumeContext, eventCode);
                        if (createMaxTable != null) {
                            long TimeMillisMaxTable = DateUtils.parse(createMaxTable, DateUtils.DATE_FULL_FORMAT).getTime();
                            //  如果 结束时间比 最大的数据时间还大，使用 最大的数据时间
                            if (TimeMillisMaxTable < end) {
                                end = TimeMillisMaxTable;
                            }
                        }

                        // 如果设置了“结束时间”
                        if (endTimeMillis != null && endTimeMillis > 0) {
                            if (begin >= endTimeMillis) {
                                return;
                            } else {
                                if (end >= endTimeMillis) {
                                    end = endTimeMillis - 1000l;
                                }
                            }
                        }

                        if (begin >= end) {
                            return;
                        }

                        beginGolab.set(end);
                        Long curCount = count.addAndGet(1);
                        // 当前时间 要大于 结束时间 ，避免数据重复抽取 ，1 秒是为了避免时间偏差
                        DateFormat timeDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String timeStr_begin = timeDf.format(new Date(begin));
                        String timeStr_end = timeDf.format(new Date(end));
                        // 1: 先记录日志
                        IbdTransterData ibdTransterData = new IbdTransterData(appCode, ownerCode, eventCode, DatabaseTypeEnum.MYSQL.getCode());
                        ibdTransterData.setConditionStime(timeStr_begin);
                        ibdTransterData.setConditionEtime(timeStr_end);
                        ibdTransterData.setRemark("");
                        ibdTransterData.setDataCount(0l);
                        String thisLogId = IbdTransterDataDao.addIbdTransterData(ibdTransterData, isCur, curCount);

                        // 2: 查询数据，并导入到channel中
                        List<Event> dataList;
                        try {
                            Long beginSearch = begin;
                            if (curCount > 1) {
                                beginSearch = begin - Constants.FLUME_DATA_SAVETIME * 1000l;
                            }

                            String timeStr_begin_search = timeDf.format(new Date(beginSearch));
                            dataList = MysqlDataDao.getMysqlData(flumeContext, timeStr_begin_search, timeStr_end, eventCode);
                            LOGGER.info("@@@ time : " + timeStr_begin + " " + timeStr_end + "  count= " + dataList.size());
                        } catch (Exception e) {
                            e.printStackTrace();
                            LOGGER.error("@@@ search error : " + e.getMessage());
                            ibdTransterData.setId(thisLogId);
                            ibdTransterData.setStatus(DataStatusEnum.ERROR_QUERY.getCode());
                            ibdTransterData.setRemark(DataStatusEnum.ERROR_QUERY.getName());
                            IbdTransterDataDao.updateIbdTransterDataById(ibdTransterData);
                            return;
                        }
                        try {
                            sendMassageTochannel(dataList);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LOGGER.error("@@@ import error : " + e.getMessage());
                            ibdTransterData.setId(thisLogId);
                            ibdTransterData.setStatus(DataStatusEnum.ERROR_IMPORT.getCode());
                            ibdTransterData.setDataCount(dataList != null ? (long) dataList.size() : 0l);
                            ibdTransterData.setRemark(DataStatusEnum.ERROR_IMPORT.getName());
                            IbdTransterDataDao.updateIbdTransterDataById(ibdTransterData);
                            return;
                        }

                        // 3： 再记录日志（修改成功标记位）
                        ibdTransterData.setId(thisLogId);
                        ibdTransterData.setDataCount(dataList != null ? (long) dataList.size() : 0l);
                        ibdTransterData.setStatus(DataStatusEnum.SUCCESS.getCode());
                        IbdTransterDataDao.updateIbdTransterDataById(ibdTransterData);


                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.error("@@@ errorMsg: " + e.getMessage());
                    }
                }
            }, 5, threadInteralSec, TimeUnit.SECONDS);
        }

        // 错误重试机制线程池
        errorRetryExecutorService = Executors.newScheduledThreadPool(1);
        errorRetryExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    DateFormat timeDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    // 前 3 分钟 到 2 小时 没有导入成功的数据
                    String stime = timeDf.format(new Date((new Date()).getTime() - 120 * 60 * 1000L));
                    String etime = timeDf.format(new Date((new Date()).getTime() - 3 * 60 * 1000L));
                    // error失败的记录（status < 0）或者 init后没有导入的记录
                    List<IbdTransterData> dataList = IbdTransterDataDao.getExceptionRetryDataByEventCode(appCode, ownerCode, eventCode, stime, etime);
                    // 集中处理“导入失败”的记录！
                    if (dataList != null && dataList.size() > 0) {
                        for (IbdTransterData thisIbdTransterData : dataList) {
                            if (thisIbdTransterData != null) {
                                List<Event> retryDataList = null;
                                try {
                                    String thisConditionStime = thisIbdTransterData.getConditionStime();
                                    String thisConditionEtime = thisIbdTransterData.getConditionEtime();
                                    retryDataList = MysqlDataDao.getMysqlData(flumeContext, thisConditionStime, thisConditionEtime, eventCode);
                                    sendMassageTochannel(retryDataList);
                                    // 记录日志（修改成功标记位）
                                    thisIbdTransterData.setDataCount(retryDataList != null ? (long) retryDataList.size() : 0l);
                                    thisIbdTransterData.setStatus(DataStatusEnum.SUCCESS.getCode());
                                    thisIbdTransterData.setRetry(thisIbdTransterData.getRetry() + 1);
                                    IbdTransterDataDao.updateIbdTransterDataById(thisIbdTransterData);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    String errorParam = "[AreaType: " + thisIbdTransterData.getAppCode() + ", ownerCode: " + thisIbdTransterData.getOwnerCode() + ", logType: " + thisIbdTransterData.getEventCode() + " , stime: " + thisIbdTransterData.getConditionStime() + ", etime: " + thisIbdTransterData.getConditionEtime() + "]";
                                    LOGGER.error("@@@ retry error : " + errorParam);
                                    LOGGER.error("@@@ retry errorMsg: " + e.getMessage());
                                    // 记录日志数据（修改"retry"次数，加 1 ）
                                    // thisIbdTransterData.setId(thisIbdTransterData.getId());
                                    thisIbdTransterData.setDataCount(retryDataList != null ? (long) retryDataList.size() : 0l);
                                    thisIbdTransterData.setRetry(thisIbdTransterData.getRetry() + 1);
                                    IbdTransterDataDao.updateIbdTransterDataById(thisIbdTransterData);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, errorRetryInitSec, errorRetryInteralSec, TimeUnit.SECONDS);

        super.start();
    }


    private void sendMassageTochannel(List<Event> retryEventList) {

        if (retryEventList.size() > 10000) {
            int startIdx = 0;
            int importCount = 10000;
            for (; startIdx < retryEventList.size(); startIdx += 10000) {
                if (startIdx + 10000 > retryEventList.size()) {
                    importCount = retryEventList.size() - startIdx;
                }
                channelProcessor.processEventBatch(retryEventList.subList(startIdx, startIdx + importCount));
            }
        } else if (retryEventList.size() > 0) {
            channelProcessor.processEventBatch(retryEventList);
        }
    }


    /**
     * Stop the source. Close database connection and stop metrics counter.
     */
    @Override
    public void stop() {
        scheduledExecutorService.shutdown();
        errorRetryExecutorService.shutdown();
        super.stop();
    }


    public static void main(String[] args) throws EventDeliveryException, InterruptedException {
        Map<String, Integer> lastDataMap = new HashMap<>();

        System.out.println(lastDataMap.get("ddddddddddd"));
//        Runtime.getRuntime().addShutdownHook(new Thread(){
//            public void run(){
//                try{
//                    mongoClient.close();
//                    System.out.println("The JVM Hook is execute");
//                }catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }


}
