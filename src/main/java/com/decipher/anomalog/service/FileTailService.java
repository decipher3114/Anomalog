package com.decipher.anomalog.service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

import com.decipher.anomalog.config.LogSourceProperties;
import com.decipher.anomalog.model.LogEntry;
import com.decipher.anomalog.parser.LogParser;

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
        Path path = Path.of(properties.getPath());
        int pollInterval = properties.getPollingIntervalMs();

        log.info("Waiting for log file: {}", path);

        try {
            while (running) {
                if (!Files.exists(path)) {
                    Thread.sleep(pollInterval);
                    continue;
                }

                try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "r")) {
                    log.info("Tailing file: {}", path);

                    if (properties.isStartFromEnd()) {
                        file.seek(file.length());
                    }

                    while (running && Files.exists(path)) {
                        if (file.length() < file.getFilePointer()) {
                            log.info("Log file truncated. Resetting pointer.");
                            file.seek(0);
                        }

                        String line;

                        while ((line = file.readLine()) != null) {
                            try {
                                processLine(line);
                            } catch (IllegalArgumentException e) {
                                log.warn("Skipping malformed log: {}", e.getMessage());
                            }
                        }

                        Thread.sleep(pollInterval);
                    }

                    if (!Files.exists(path)) {
                        log.info("Log file deleted. Waiting for recreation.");
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("File tailer stopped");
        } catch (IOException e) {
            log.error("I/O error while tailing {}", path, e);
        }
    }

    private void processLine(String line) throws IllegalArgumentException {
        LogEntry entry = logParser.parse(line);
        logBuffer.add(entry);
    }
}