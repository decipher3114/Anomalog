package com.decipher.anomalog.websocket;

public record WebSocketEvent<T>(
        WebSocketEventType type,
        T payload) {

}
