package org.log_analyser.utils;

import lombok.extern.java.Log;
import org.log_analyser.model.LogData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: abhirj87
 * Class to house all the help methods
 */
@Log
public class Utils {

    private static Utils utils;

    private Utils() {

    }

    public static Utils getUtils() {
        if (utils == null) {
            utils = new Utils();
        }
        return utils;
    }


    /**
     * Input log data to be processed.
     * Eg: input: --> "slip005.hol.nl - - [28/Jul/1995:13:32:01 -0400] "GET /images/p263_100.jpg HTTP/1.0" 200 49152"
     * returns: LogData object
     *
     * @param logString
     * @return
     */
    public LogData processLog(String logString) {
        String LOG_PATTREN = "^(.*?) - - \\[(.*?):(\\d+:\\d+:\\d+) -(.*?)\\] \\\"([A-Za-z]*) (.*?) (.*?)\\\" (.*) (.*)$";
        Pattern p = Pattern.compile(LOG_PATTREN);
        Matcher matcher = p.matcher(logString.trim());

        if (matcher.find()) {
            int noOfGroups = matcher.groupCount();
            String[] groups = new String[9];
            for (int i = 1; i <= noOfGroups; i++) {
                groups[i - 1] = matcher.group(i);
                log.info("group[" + i + "] : " + matcher.group(i));
            }
            LogData logData = new LogData();
            DataParser dataParser = new DataParser();
            if (dataParser.parse(groups, logData)) {
                return logData;
            }
        } else {
            log.log(java.util.logging.Level.SEVERE, "Error processing data: " + logString);
        }
        return null;
    }

    /**
     * Converts String to Int
     *
     * @param arg
     * @return
     */
    public Integer parseInt(String arg) {
        try {
            return Integer.parseInt(arg.trim());
        } catch (NumberFormatException e) {
            log.log(java.util.logging.Level.SEVERE,
                    "Unable to Parse Int number: " + arg
                            + " Message: " + e.getMessage());
        }
        return null;
    }


    public Double parseDouble(String input) {
        try {
            return Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            log.log(java.util.logging.Level.SEVERE,
                    "Unable to Parse Double number: " + input
                            + " Message: " + e.getMessage());
        }
        return null;
    }

    public Short parseShort(String input) {
        try {
            return Short.parseShort(input.trim());
        } catch (NumberFormatException e) {
            log.log(java.util.logging.Level.SEVERE,
                    "Unable to Parse Double number: " + input
                            + " Message: " + e.getMessage());
        }
        return null;
    }

    public Long parseLong(String input) {
        try {
            return Long.parseLong(input.trim());
        } catch (NumberFormatException e) {
            log.log(java.util.logging.Level.SEVERE,
                    "Unable to Parse Double number: " + input
                            + " Message: " + e.getMessage());
        }
        return null;
    }

}
