package org.log_analyser.test;

import lombok.extern.java.Log;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.log_analyser.model.LogData;
import org.log_analyser.process.TopNProcessor;
import org.log_analyser.utils.Utils;

import java.util.List;

@Log
public class TestTopNProcessorIntegration {

    private  SparkSession spark;
//    private String inputDataPath;
//    private String sparkWareHouseDir;
    private String outputTableOrPath;
//    private String master;
//    private int noOfPartitions;
//    private int topN;
//    private JavaRDD<LogData> data;
    private TopNProcessor topNProcessor;
    private JavaRDD<LogData> data;

    @Before
    public void setup(){
         String inputDataPath = System.getProperty("user.dir")+"/src/test/resources/sample_data.txt";
        String sparkWareHouseDir = System.getProperty("user.dir")+"/src/test/resources/warehouse_dir";
        outputTableOrPath = "topn_results";
        String master = "local";
        int noOfPartitions = 5;
        int topN = 3;
        log.info("inputDataPath: "+inputDataPath);
        log.info("sparkWareHouseDir: "+sparkWareHouseDir);
        log.info("outputTableOrPath: "+outputTableOrPath);


//        @lombok.Cleanup
        spark = SparkSession.builder().master(master).appName("AccessLogProcessor")
                .config("spark.hadoop.validateOutputSpecs", "false")
                .config("spark.driver.host", "localhost")
                .config("spark.sql.warehouse.dir",sparkWareHouseDir)
                .getOrCreate();

        data = spark.sparkContext()
                .textFile(inputDataPath, noOfPartitions)
                .toJavaRDD()
                .map(x -> Utils.getUtils().processLog(x));
         topNProcessor = new TopNProcessor(spark, data, topN, outputTableOrPath);
    }

    @Test
    public void testTopNProcessor(){
        /**
         * This method is slightly faster, Hence using it
         */
        topNProcessor.setOutputTableName(System.getProperty("user.dir") +"/src/test/resources/output/"+outputTableOrPath);
        topNProcessor.findTopN();
        List<String>result =  spark.sparkContext()
                .textFile(System.getProperty("user.dir") +"/src/test/resources/output/"+outputTableOrPath,1)
                .toJavaRDD().collect();

        log.info("result length: "+result.size());
        assert (result.size()==8);
        result.forEach(x -> {
            if(x.contains("28/Jul/1995") && x.contains("163.205.53.14")){
                assert (x.contains(",6"));
            }
        });


    }
    @Test
    public void testTopNProcessorNBySql(){
       /**
         * This is slightly slower compared to the above one hence commented out
         * But the logic is simpler to understand
         */
        topNProcessor.findTopNBySql();
        Dataset res = spark.sql("select count from topn_results where logDate='28/Jul/1995' and ipAddress='163.205.53.14'");
        assert (res.count() == 1l);
        log.info("result:  "+res);
        //assert that the count is 6
        assert ("[6]".contentEquals(res.collectAsList().get(0).toString()));
    }

    @After
    public void cleanUp(){
        spark.close();
        spark.stop();
    }

}
