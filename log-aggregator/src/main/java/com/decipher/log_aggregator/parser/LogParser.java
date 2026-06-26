package com.decipher.log_aggregator.parser;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.decipher.log_aggregator.model.LogEntry;
import com.decipher.log_aggregator.model.LogLevel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LogParser {

    public LogEntry parse(String line) throws IllegalArgumentException {

        String[] parts = line.split(" ", 3);

        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Invalid log format: " + line);
        }

        Instant timestamp;
        try {
            timestamp = Instant.parse(parts[0]);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Invalid timestamp: " + parts[0],
                    e);
        }

        LogLevel level;
        try {
            level = LogLevel.valueOf(parts[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid log level: " + parts[1],
                    e);
        }

        return new LogEntry(
                timestamp,
                level,
                parts[2]);
    }

}
