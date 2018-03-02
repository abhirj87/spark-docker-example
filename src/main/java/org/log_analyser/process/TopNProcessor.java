package org.log_analyser.process;

import lombok.extern.java.Log;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.log_analyser.model.LogData;
import org.log_analyser.utils.Utils;
import org.spark_project.guava.collect.Lists;
import scala.Serializable;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

/**
 * author: abhirj87
 * Logic to process topN visitors per day
 */
@Log
@lombok.AllArgsConstructor
@lombok.Setter
public class TopNProcessor implements Serializable {

    transient private SparkSession spark;
    private JavaRDD<LogData> data;
    private Integer topN;
    private String outputTableName;

    /**
     * This method will take LogData java RDD and store topN IP addresses(visitors) per day
     * This uses spark sql to get the results and every time overwrites the result table.
     */
    public void findTopNBySql() {


        long l1 = System.currentTimeMillis();

        Dataset<Row> logData = spark.createDataFrame(data
                .filter(x -> (x != null && x.getIpAddress() != null && x.getLogDate() != null)), LogData.class);
        logData.select("ipAddress", "logDate").groupBy("ipAddress", "logDate")
                .count()
                .registerTempTable(" url_counts");

        /**
         * Splitting the sql query as rank function is not available in the above format
         */
        Dataset<Row> result = spark.sql("select * from " +
                "(select logDate,ipAddress," +
                "count, Rank() over (partition by logDate Order by count desc) as rank " +
                "from url_counts) " +
                "where rank <= " + topN);

        result.write().mode("overwrite").saveAsTable(outputTableName);
        log.info("Output result available in the table: " + outputTableName);
        long l2 = System.currentTimeMillis();
        log.info("time taken: " + (l2 - l1));
    }


    /**
     * This method will take LogData java RDD and store topN IP addresses(visitors) per day
     * This uses spark sql to get the results and every time overwrites the result table.
     */
    public void findTopN() {


        long l1 = System.currentTimeMillis();
        if (data != null) {
            data.filter(x -> (x != null && x.getIpAddress() != null && x.getLogDate() != null))
                    .mapToPair(x -> new Tuple2<>(x.getLogDate() + "," + x.getIpAddress(), 1))
                /*
                (28/Jul/1995,slip005.hol.nl,1)+(28/Jul/1995,slip005.hol.nl,1)+(28/Jul/1995,slip005.hol.nl,1)
                ==> (key: [28/Jul/1995,slip005.hol.nl],value: 3)

                 */
                    .reduceByKey((x, y) -> x + y)
                 /*
                    now Key changed: (Key: 28/Jul/1995, value: [slip005.hol.nl,3])
                  */
                    .mapToPair(x -> new Tuple2<>(x._1().split(",")[0], x._1().split(",")[1] + "," + x._2()))
                    .groupByKey()
                /*
                   this make the data look like so:
                   (Key: 28/Jul/1995, [value1: [slip005.hol.nl,3],value2: [slip005.hol.dd,8],value3: [slip005.eee,3]])
                 */
                    .map(x -> {
                        List<String> values = Lists.newArrayList(x._2());
                    /*
                       Sorting the groupings:
                       Extract the counts and compare it
                     */
                        values.sort(new Comparator() {
                            @Override
                            public int compare(Object o1, Object o2) {
                                int v1 = Utils.getUtils().parseInt(((String) o1).split(",")[1]);
                                int v2 = Utils.getUtils().parseInt(((String) o2).split(",")[1]);

                                return v2 - v1;
                            }
                        });

                    /*
                    Cut the list to topN visitors
                     */

                        int size = values.size();
                        for (int i = size - 1; i >= topN; i--) {
                            values.remove(i);
                        }
                        return new Tuple2<String, Iterable<String>>(x._1(), (Iterable) values);

                    })
                    .flatMap((FlatMapFunction<Tuple2<String, Iterable<String>>, String>)
                            stringIterableTuple2 -> {
                                List<String> l = new ArrayList();
                                stringIterableTuple2._2.forEach(y -> l.add(stringIterableTuple2._1() + "," + y));
                                return l.iterator();
                            })
                    .saveAsTextFile(outputTableName);
        } else {
            log.log(Level.SEVERE, "No data present in the RDD: " + data);
        }

        log.info("Output result available in the table: " + outputTableName);
        long l2 = System.currentTimeMillis();
        log.info("time taken: " + (l2 - l1));

    }


}
