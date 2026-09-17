package com.decipher.anomalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.decipher.anomalog.config.AnomalyDetectionProperties;
import com.decipher.anomalog.config.LogSourceProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({ LogSourceProperties.class, AnomalyDetectionProperties.class })
public class AnomalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnomalogApplication.class, args);
    }

}
