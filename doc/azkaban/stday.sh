#!/bin/sh
start=$(date  +"%Y-%m-%d" -d  "-31 minute")
end=$(date  +"%Y-%m-%d" -d  "1 days")
/opt/cloudera/parcels/CDH-6.2.0-1.cdh6.2.0.p0.967373/lib/spark/bin/spark-submit --class  com.square.jobs.kpi.StDayMain --master yarn --deploy-mode client --driver-memory 2g --executor-memory 2g --num-executors 2 --executor-cores 1 jobs-1.0-SNAPSHOT.jar $start $end