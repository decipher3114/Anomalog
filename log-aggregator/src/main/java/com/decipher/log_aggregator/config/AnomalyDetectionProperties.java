package com.decipher.log_aggregator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "anomaly.detection")
public class AnomalyDetectionProperties {
    private int windowSize;
    private int historySize;
    private double spikeThreshold;

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public int getHistorySize() {
        return historySize;
    }

    public void setHistorySize(int historySize) {
        this.historySize = historySize;
    }

    public double getSpikeThreshold() {
        return spikeThreshold;
    }

    public void setSpikeThreshold(double spikeThreshold) {
        this.spikeThreshold = spikeThreshold;
    }

}
