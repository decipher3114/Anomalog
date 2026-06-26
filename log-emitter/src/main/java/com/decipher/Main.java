package com.decipher;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.List;
import java.util.Random;

public class Main {

    private static final Path LOG_FILE = Path.of("../app.log");

    private static final Random RANDOM = new Random();

    private static BufferedWriter writer;

    private static final List<String> INFO_MESSAGES = List.of(
            "User login successful",
            "Request completed",
            "Cache hit",
            "Order created",
            "Connection established",
            "Heartbeat received",
            "Profile updated",
            "Payment processed",
            "Background job completed",
            "Session refreshed");

    private static final List<String> WARN_MESSAGES = List.of(
            "Database query took longer than expected",
            "Retrying failed request",
            "Cache miss",
            "Disk usage above 80%",
            "High memory usage detected",
            "Slow API response",
            "Connection pool running low",
            "Configuration value missing, using default");

    private static final List<String> ERROR_MESSAGES = List.of(
            "Database timeout",
            "Redis connection lost",
            "Unhandled exception",
            "Payment gateway unavailable",
            "Authentication failed",
            "Service unavailable",
            "Failed to write file",
            "Out of memory while processing request");

    public static void main(String[] args) throws Exception {

        Files.createDirectories(LOG_FILE.getParent());

        writer = Files.newBufferedWriter(
                LOG_FILE,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                writer.flush();
                writer.close();
            } catch (IOException ignored) {
            }
        }));

        System.out.println("Writing logs to " + LOG_FILE.toAbsolutePath());

        long lastFlush = System.currentTimeMillis();

        while (true) {

            emitNormalTraffic();

            // ~3% chance of anomaly
            if (RANDOM.nextInt(100) < 3) {
                emitErrorSpike();
            }

            if (System.currentTimeMillis() - lastFlush >= 1000) {
                writer.flush();
                lastFlush = System.currentTimeMillis();
            }

            Thread.sleep(150);
        }
    }

    private static void emitNormalTraffic() throws Exception {

        int count = RANDOM.nextInt(5) + 1;

        for (int i = 0; i < count; i++) {

            int x = RANDOM.nextInt(100);

            if (x < 75) {
                write("INFO", random(INFO_MESSAGES));
            } else if (x < 95) {
                write("WARN", random(WARN_MESSAGES));
            } else {
                write("ERROR", random(ERROR_MESSAGES));
            }
        }
    }

    private static void emitErrorSpike() throws Exception {

        System.out.println(">>> ERROR SPIKE <<<");

        int count = RANDOM.nextInt(30) + 20;

        for (int i = 0; i < count; i++) {

            write("ERROR", random(ERROR_MESSAGES));

            Thread.sleep(RANDOM.nextInt(30) + 10);
        }
    }

    private static void write(String level, String message)
            throws IOException {

        writer.write("%s %s %s%n".formatted(
                Instant.now(),
                level,
                message));
    }

    private static String random(List<String> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }
}