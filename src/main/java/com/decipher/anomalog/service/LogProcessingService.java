package com.decipher.anomalog.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.decipher.anomalog.model.BatchMetrics;
import com.decipher.anomalog.model.LogEntry;
import com.decipher.anomalog.websocket.WebSocketEvent;
import com.decipher.anomalog.websocket.WebSocketEventType;
import com.decipher.anomalog.websocket.WebSocketPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogProcessingService {

    public final LogIngestionService logIngestion;
    private final MetricsService metrics;
    private final AnomalyDetectionService anomalyDetection;

    private final WebSocketPublisher publisher;

    @Scheduled(fixedRate = 1000)
    public void processLogs() {
        List<LogEntry> logs = logIngestion.drain();

        if (logs.isEmpty()) {
            return;
        }

        BatchMetrics batch = metrics.addBatch(logs);

        publisher.publish(
                new WebSocketEvent<>(
                        WebSocketEventType.BATCH_PROCESSED,
                        batch));

        anomalyDetection.observe(
                batch.timestamp(),
                batch.errorCount());

        log.info(
                "Processed {} logs (errors={})",
                batch.logCount(),
                batch.errorCount());
    }
}
