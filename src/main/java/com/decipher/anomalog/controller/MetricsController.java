package com.decipher.anomalog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.decipher.anomalog.model.MetricsSnapshot;
import com.decipher.anomalog.service.MetricsService;

@RestController
public class MetricsController {
    public final MetricsService metrics;

    public MetricsController(MetricsService metrics) {
        this.metrics = metrics;
    }

    @GetMapping("/metrics")
    public MetricsSnapshot getMetrics() {
        return metrics.snapshot();
    }

}
