package org.log_analyser;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;
import org.log_analyser.model.LogData;
import org.log_analyser.process.TopNProcessor;
import org.log_analyser.utils.Utils;

/**
 * Spark Driver program for finding TopN visitors per day
 * Usage: inputDataPath outputTableOrPath master noOfPartitions topN
 * <p>
 * author: abhirj87
 */
@Log
public class AccessLogProcessor {

    @SneakyThrows
    public static void main(String[] args) throws org.apache.spark.sql.AnalysisException {
        runDriver(args);
    }

    public static void runDriver(String[] args){
        String inputDataPath = "data/sample_data.txt";
        String outputTableOrPath = "topn_results";
        String master = "local";
        int noOfPartitions = 5;
        int topN = 3;

        if (args.length == 5) {
            inputDataPath = args[0];
            outputTableOrPath = args[1];
            master = args[2];
            noOfPartitions = Utils.getUtils().parseInt(args[3]);
            topN = Utils.getUtils().parseInt(args[4]);
        } else if (args.length == 0) {
            log.info("No paramters given, hence defaults assumed!! " +
                    "\n inputDataPath: " + inputDataPath +
                    "\n outputTableOrPath: " + outputTableOrPath +
                    "\n master: " + master +
                    "\n noOfPartitions: " + noOfPartitions +
                    "\n topN: " + topN);

        } else {
            log.log(java.util.logging.Level.WARNING, "Invalid input parameters!!");
            log.log(java.util.logging.Level.WARNING, "Usage: inputDataPath outputTableOrPath master noOfPartitions topN");
        }


        Logger.getLogger("akka").setLevel(Level.OFF);
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("org.log_analyser").setLevel(Level.INFO);
        Logger.getLogger("org.log_analyser.utils").setLevel(Level.OFF);

        /**
         * make sparkSession auto closable with lombok annotation
         */

        @lombok.Cleanup
        SparkSession spark = SparkSession.builder().master(master).appName("AccessLogProcessor")
                .config("spark.hadoop.validateOutputSpecs", "false")
                .config("spark.driver.host", "localhost")
                .getOrCreate();


        JavaRDD<LogData> data = spark.sparkContext()
                .textFile(inputDataPath, noOfPartitions)
                .toJavaRDD()
                .map(x -> Utils.getUtils().processLog(x));


        TopNProcessor topNProcessor = new TopNProcessor(spark, data, topN, outputTableOrPath);

        /**
         * This method is slightly faster, Hence using it
         */
        topNProcessor.findTopN();

        /**
         * This is slightly slower compared to the above one hence commented out
         * But the logic is simpler to understand
         */
//        topNProcessor.findTopNBySql();

    }


}
