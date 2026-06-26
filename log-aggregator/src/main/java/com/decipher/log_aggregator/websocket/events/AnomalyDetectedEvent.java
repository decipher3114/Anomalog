package com.decipher.log_aggregator.websocket.events;

import com.decipher.log_aggregator.model.anomaly.Anomaly;

public record AnomalyDetectedEvent(
        Anomaly anomaly) {

}
