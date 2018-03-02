#!/bin/bash
#remove spark container if it exits
docker rm spark
FILE=target/NASA_access_log_Jul95
if [ ! -f "$FILE" ]; then
   curl -O ftp://ita.ee.lbl.gov/traces/NASA_access_log_Jul95.gz
   gunzip NASA_access_log_Jul95.gz
   mv NASA_access_log_Jul95 target/
fi

FILE=target/start_job.sh
cp -f src/main/resources/start_job.sh target/
cp -f src/main/resources/interpreter.json target/
cp -f src/main/resources/zeppelin-site.xml target/


docker build -t abhirj87/apache_spark:latest -t abhirj87/apache_spark:2.1.0 .
docker run -it -p 4040:4040 -p 8080:8080 -p 8081:8081 --name=spark -h spark abhirj87/apache_spark:2.1.0