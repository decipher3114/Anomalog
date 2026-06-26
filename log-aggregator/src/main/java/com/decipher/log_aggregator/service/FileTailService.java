package com.decipher.log_aggregator.service;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.springframework.stereotype.Service;

import com.decipher.log_aggregator.config.LogSourceProperties;
import com.decipher.log_aggregator.model.LogEntry;
import com.decipher.log_aggregator.parser.LogParser;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileTailService {
    private final LogSourceProperties properties;
    private final LogIngestionService logBuffer;
    private final LogParser logParser;

    private Thread tailThread;
    private volatile boolean running = true;

    @PostConstruct
    public void start() {
        tailThread = new Thread(this::tailLoop, "file-tailer");
        log.info("Starting file tailer");
        tailThread.start();
    }

    @PreDestroy
    public void stop() {
        log.info("Stopping file tailer");
        running = false;

        if (tailThread != null) {
            tailThread.interrupt();
        }

    }

    public void tailLoop() {
        log.info("Tailing file: {}", properties.getPath());
        try (RandomAccessFile file = new RandomAccessFile(properties.getPath(), "r")) {

            if (properties.isStartFromEnd()) {
                file.seek(file.length());
            }

            while (running) {
                String line;

                if (file.length() < file.getFilePointer()) {
                    log.info("Log file truncated. Resetting pointer.");
                    file.seek(0);
                }

                while ((line = file.readLine()) != null) {
                    try {
                        processLine(line);
                    } catch (IllegalArgumentException e) {
                        log.warn("Skipping malformed log: {}", e.getMessage());
                    }
                }

                Thread.sleep(properties.getPollingIntervalMs());
            }

        } catch (IOException e) {
            log.error("I/O error while tailing {}", properties.getPath(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("File tailer stopped");
        }
    }

    private void processLine(String line) throws IllegalArgumentException {
        LogEntry entry = logParser.parse(line);
        logBuffer.add(entry);
    }
}
