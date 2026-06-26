package com.decipher.log_aggregator.model;

import java.time.Instant;

public record BatchMetrics(
        Instant timestamp,
        long logCount,
        long errorCount) {

}
