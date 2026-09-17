package com.decipher.anomalog.websocket.events;

import com.decipher.anomalog.model.BatchMetrics;

public record BatchProcessedEvent(BatchMetrics batch) {

}
