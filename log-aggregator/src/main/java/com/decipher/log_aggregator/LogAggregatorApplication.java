package com.decipher.log_aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.decipher.log_aggregator.config.AnomalyDetectionProperties;
import com.decipher.log_aggregator.config.LogSourceProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({ LogSourceProperties.class, AnomalyDetectionProperties.class })
public class LogAggregatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogAggregatorApplication.class, args);
    }

}
