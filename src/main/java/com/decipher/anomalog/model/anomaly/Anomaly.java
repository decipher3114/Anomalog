package com.decipher.anomalog.model.anomaly;

import java.time.Instant;

public record Anomaly(
        Instant timestamp,
        AnomalyType type,
        long currentValue,
        double averageValue) {
}
