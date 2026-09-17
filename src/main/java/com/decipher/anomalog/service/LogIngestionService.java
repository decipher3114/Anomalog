package com.decipher.anomalog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Service;
import com.decipher.anomalog.model.LogEntry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LogIngestionService {

    private final ConcurrentLinkedQueue<LogEntry> queue = new ConcurrentLinkedQueue<>();

    public void add(LogEntry entry) {
        queue.add(entry);
    }

    public List<LogEntry> drain() {
        List<LogEntry> logs = new ArrayList<>();

        LogEntry entry;

        while ((entry = queue.poll()) != null) {
            logs.add(entry);
        }

        log.info("Drained {} logs from ingestion queue", logs.size());

        return logs;
    }

    public int size() {
        return queue.size();
    }
}
