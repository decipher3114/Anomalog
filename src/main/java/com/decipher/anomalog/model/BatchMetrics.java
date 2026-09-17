package com.decipher.anomalog.model;

import java.time.Instant;

public record BatchMetrics(
        Instant timestamp,
        long logCount,
        long errorCount) {
}
