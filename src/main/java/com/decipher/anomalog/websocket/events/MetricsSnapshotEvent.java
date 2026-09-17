package com.decipher.anomalog.websocket.events;

import com.decipher.anomalog.model.MetricsSnapshot;

public record MetricsSnapshotEvent(MetricsSnapshot metrics) {

}
