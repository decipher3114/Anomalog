package com.decipher.anomalog.service;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.springframework.stereotype.Service;
import com.decipher.anomalog.config.AnomalyDetectionProperties;
import com.decipher.anomalog.model.anomaly.Anomaly;
import com.decipher.anomalog.model.anomaly.AnomalyType;
import com.decipher.anomalog.websocket.WebSocketEvent;
import com.decipher.anomalog.websocket.WebSocketEventType;
import com.decipher.anomalog.websocket.WebSocketPublisher;
import com.decipher.anomalog.websocket.events.AnomalyDetectedEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AnomalyDetectionService {
    private final AnomalyDetectionProperties properties;

    private final WebSocketPublisher publisher;

    private final Deque<Long> errorCounts;
    private final Deque<Anomaly> anomalies;

    public AnomalyDetectionService(
            AnomalyDetectionProperties properties,
            WebSocketPublisher publisher) {
        this.properties = properties;
        this.publisher = publisher;

        this.errorCounts = new ArrayDeque<>(properties.getWindowSize());
        this.anomalies = new ArrayDeque<>(properties.getHistorySize());
    }

    public void observe(Instant timestamp, long errors) {
        if (errorCounts.size() == properties.getWindowSize()) {
            double average = errorCounts.stream().mapToLong(x -> x).average()
                    .orElse(0);

            if (errors > (average * 3)) {
                // Anomaly detected
                Anomaly anomaly = new Anomaly(
                        timestamp,
                        AnomalyType.ERROR_SPIKE,
                        errors,
                        average);

                if (anomalies.size() == properties.getHistorySize()) {
                    anomalies.removeFirst();
                }

                anomalies.addLast(anomaly);

                publisher.publish(new WebSocketEvent<>(
                        WebSocketEventType.ANOMALY_DETECTED,
                        new AnomalyDetectedEvent(anomaly)));

            }

            errorCounts.removeFirst();

        }

        errorCounts.addLast(errors);
    }

    public List<Anomaly> getAnomalies() {
        return List.copyOf(anomalies);
    }

}
