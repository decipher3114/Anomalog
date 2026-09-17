package com.decipher.anomalog.model;

import java.time.Instant;
import java.util.Map;

public record MetricsSnapshot(
        Instant lastProcessedAt,
        long logCount,
        Map<LogLevel, Long> perLevelCount) {
}
