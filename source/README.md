## source

#### mysql
```
use flume_source_conf ; 
drop table  ibd_transter_data ;
CREATE TABLE `ibd_transter_data` (
  `id` varchar(100)   DEFAULT '' COMMENT 'id',
  `app_code` varchar(100)  DEFAULT '' COMMENT 'app标识',
  `owner_code` varchar(100)  DEFAULT '' COMMENT '业主标识',
  `event_code` varchar(100)  DEFAULT '' COMMENT '日志标识',
  `data_source` varchar(100)  DEFAULT '' COMMENT '数据来源',
  `condition_stime` datetime  DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
  `condition_etime` datetime  DEFAULT CURRENT_TIMESTAMP COMMENT '结束时间',
  `data_count` bigint(20)  DEFAULT '0' COMMENT '数据个数',
  `status` int(4)  DEFAULT '0' COMMENT '导入状态',
  `remark` varchar(100)  DEFAULT '' COMMENT '备注',
  `retry` int(11)  COMMENT '重试次数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime  DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  UNIQUE KEY `index_unique` (`id`),
  KEY `app_code` (`app_code`),
  KEY `owner_code` (`owner_code`),
  KEY `event_code` (`event_code`),
  KEY `data_source` (`data_source`),
  KEY `condition_stime` (`condition_stime`),
  KEY `condition_etime` (`condition_etime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='flume配置表';
```

#### flume  配置
```
agent.sources = r1
agent.channels = c1
agent.sinks = k1

agent.sources.r1.type =com.square.source.threads.MysqlDataThread
agent.sources.r1.mysql.driver.class=com.mysql.jdbc.Driver
agent.sources.r1.mysql.data.url=jdbc:mysql://10.10.95.175:8306/pig?useUnicode=true&characterEncoding=utf8&mysqlEncoding=utf8&autoReconnect=true&failOverReadOnly=false&maxReconnects=10&allowMultiQueries=true&tinyInt1isBit=false
agent.sources.r1.mysql.data.username=reader
agent.sources.r1.mysql.data.password=reader
agent.sources.r1.mysql.data.sql=select * from sk_finance_recharge_chanel_types where  last_update_time >='${start_time}' and  last_update_time<'${end_time}'
agent.sources.r1.startTime=2019-03-12 12:09:11
agent.sources.r1.endTime=3000-07-12 13:42:00
agent.sources.r1.threadCount=5
agent.sources.r1.threadInteralSec=10
agent.sources.r1.queryInteralSec=600
agent.sources.r1.appCode=test1
agent.sources.r1.ownerCode=test1
agent.sources.r1.eventCode=order

agent.channels.c1.type = file
agent.channels.c1.capacity = 10000
agent.channels.c1.transactionCapacity = 100

agent.sinks.k1.type = org.apache.flume.sink.kafka.KafkaSink
agent.sinks.k1.brokerList = hadoop:9092
agent.sinks.k1.topic = test

agent.sources.r1.channels = c1
agent.sinks.k1.channel = c1

```