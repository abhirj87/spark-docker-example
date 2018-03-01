package org.log_analyser.test;

import org.junit.Test;
import org.log_analyser.model.LogData;
import org.log_analyser.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {
    @Test
    public void testLogProcessor() throws IOException {

        List<String> inputData = Files.readAllLines(Paths.get(System.getProperty("user.dir") + "/src/test/resources/sample_data.txt"));
        List<String> expectedOutput = Files.readAllLines(Paths.get(System.getProperty("user.dir") + "/src/test/resources/parse_result/results"));

        List<LogData> logData = new ArrayList<>();
        inputData.forEach(x -> logData.add(Utils.getUtils().processLog(x)));

        assert (logData.size() == expectedOutput.size());


        int i = 0;
        for (LogData logDatum : logData) {
            assert (logDatum.toString().contentEquals(expectedOutput.get(i++)));
        }
    }


}
