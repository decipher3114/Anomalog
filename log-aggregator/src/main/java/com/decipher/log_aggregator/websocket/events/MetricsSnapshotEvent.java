package com.decipher.log_aggregator.websocket.events;

import com.decipher.log_aggregator.model.MetricsSnapshot;

public record MetricsSnapshotEvent(MetricsSnapshot metrics) {

}
