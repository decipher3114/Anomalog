package com.decipher.anomalog.service;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.decipher.anomalog.model.BatchMetrics;
import com.decipher.anomalog.model.LogEntry;
import com.decipher.anomalog.model.LogLevel;
import com.decipher.anomalog.model.MetricsSnapshot;
import com.decipher.anomalog.websocket.WebSocketEvent;
import com.decipher.anomalog.websocket.WebSocketEventType;
import com.decipher.anomalog.websocket.WebSocketPublisher;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final WebSocketPublisher publisher;

    private final EnumMap<LogLevel, Long> perLevelCount = new EnumMap<>(LogLevel.class);

    private long logCount;
    private Instant lastProcessedAt;

    {
        for (LogLevel level : LogLevel.values()) {
            perLevelCount.put(level, 0L);
        }
    }

    public synchronized BatchMetrics addBatch(List<LogEntry> logs) {

        long errorCount = 0;

        for (LogEntry log : logs) {

            LogLevel level = log.level();

            logCount++;
            perLevelCount.put(
                    level,
                    perLevelCount.get(level) + 1);

            if (level == LogLevel.ERROR) {
                errorCount++;
            }
        }

        lastProcessedAt = Instant.now();

        return new BatchMetrics(
                lastProcessedAt,
                logs.size(),
                errorCount);
    }

    public synchronized long getLogCount() {
        return logCount;
    }

    public synchronized long getCountByLevel(LogLevel level) {
        return perLevelCount.get(level);
    }

    public synchronized MetricsSnapshot snapshot() {

        return new MetricsSnapshot(
                lastProcessedAt,
                logCount,
                Map.copyOf(perLevelCount));
    }

    @Scheduled(fixedRate = 60, timeUnit = TimeUnit.SECONDS)
    public void publishSnapshot() {

        publisher.publish(
                new WebSocketEvent<>(
                        WebSocketEventType.METRICS_SNAPSHOT,
                        snapshot()));
    }
}