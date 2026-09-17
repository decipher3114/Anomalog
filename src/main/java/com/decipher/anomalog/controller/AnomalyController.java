package com.decipher.anomalog.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.decipher.anomalog.model.anomaly.Anomaly;
import com.decipher.anomalog.service.AnomalyDetectionService;

@RestController
public class AnomalyController {
    public final AnomalyDetectionService anomaly;

    public AnomalyController(AnomalyDetectionService anomaly) {
        this.anomaly = anomaly;
    }

    @GetMapping("/anomalies")
    public List<Anomaly> anomalies() {
        return anomaly.getAnomalies();
    }
}
