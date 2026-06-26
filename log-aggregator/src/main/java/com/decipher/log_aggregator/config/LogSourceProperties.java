package com.decipher.log_aggregator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "log.source")
public class LogSourceProperties {
    private String path;
    private int pollingIntervalMs;
    private boolean startFromEnd;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPollingIntervalMs() {
        return pollingIntervalMs;
    }

    public void setPollingIntervalMs(int pollingIntervalMs) {
        this.pollingIntervalMs = pollingIntervalMs;
    }

    public boolean isStartFromEnd() {
        return startFromEnd;
    }

    public void setStartFromEnd(boolean startFromEnd) {
        this.startFromEnd = startFromEnd;
    }

}
