package com.decipher.anomalog.websocket.events;

import com.decipher.anomalog.model.anomaly.Anomaly;

public record AnomalyDetectedEvent(Anomaly anomaly) {

}
