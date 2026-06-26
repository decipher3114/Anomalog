package com.decipher.log_aggregator.websocket;

public record WebSocketEvent<T>(
        WebSocketEventType type,
        T payload) {

}
