package org.log_analyser.model;

import java.io.Serializable;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class LogData implements Serializable {
    private String ipAddress;
    private String logDate;
    private String logTime;
    private String zone;
    private String httpMethod;
    private String referalUrl;
    private String protocolVersion;
    private Integer reponseCode;
    private String objectReturned;
}
