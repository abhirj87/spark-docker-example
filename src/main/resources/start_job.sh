#!/bin/bash
#
# Spark submit command
#
spark-submit --class org.log_analyser.AccessLogProcessor --master spark://spark:7077 top_n_processor-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
input \
output_data \
spark://spark:7077 \
5 \
3

echo "Run Completed :) !!"
echo "-------SAMPLE OUTPUT-----------"
tail -n 5 output_data/*

echo "Moment of Truth: Yes its time for validation!!"
#
#Following peice of code is to validate the output
#
#
file=NASA_access_log_Jul95
for ln in $(tail -n 3 -q output_data/*)
do
dt=$(echo $ln | cut -d ',' -f1)
ip=$(echo $ln | cut -d ',' -f2)
count=$(echo $ln | cut -d ',' -f3)
#echo $ip $dt $count  all from the output
var=$(echo $(cat $file | grep $dt | grep $ip | wc -l))
#echo $var == from the input
if [ $var -eq $count ]; then
    echo "Matched!!! count from  wc command: "$var " count in o/p data: "$count
else
    echo "Validation failed!!" $var $count
    echo "Validation failed!!"
    echo "Validation failed!!"
    echo "Validation failed!!"
fi
done
exit