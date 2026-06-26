package com.decipher.log_aggregator.websocket;

import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketPublisher {
    private final EventWebSocketHandler handler;

    private final ObjectMapper mapper;

    public void publish(@NonNull WebSocketEvent<?> event) {

        try {

            String json = mapper.writeValueAsString(event);

            TextMessage message = new TextMessage(Objects.requireNonNull(json));

            for (WebSocketSession session : handler.getSessions()) {

                if (!session.isOpen()) {
                    continue;
                }

                session.sendMessage(message);
            }

        } catch (Exception e) {
            log.error("Failed to publish websocket event", e);
        }
    }

}
