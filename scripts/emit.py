#!/usr/bin/env -S uv run
# /// script
# requires-python = ">=3.12"
# dependencies = []
# ///

import argparse
import random
import time
from datetime import datetime, timezone

INFO_MESSAGES = [
    "User login successful",
    "Request completed",
    "Cache hit",
    "Order created",
    "Connection established",
    "Heartbeat received",
    "Profile updated",
    "Payment processed",
    "Background job completed",
    "Session refreshed",
    "User preferences loaded",
    "Search request completed",
    "File uploaded successfully",
    "Notification delivered",
    "Job queued successfully",
    "Worker started",
    "Worker completed task",
    "Database connection acquired",
    "API request processed",
    "Message published",
    "Message consumed",
    "Configuration loaded",
    "Health check passed",
    "Metrics batch exported",
    "User session validated",
    "Access token refreshed",
    "Cache entry created",
    "Cache entry evicted",
    "Request routed successfully",
    "Task scheduled",
]

WARN_MESSAGES = [
    "Database query took longer than expected",
    "Retrying failed request",
    "Cache miss",
    "Disk usage above 80%",
    "High memory usage detected",
    "Slow API response",
    "Connection pool running low",
    "Configuration value missing, using default",
    "Request queue depth is high",
    "Worker processing slower than expected",
    "Database connection pool nearing capacity",
    "Retry attempt nearing limit",
    "Message processing latency increased",
    "External service response is slow",
    "Thread pool utilization is high",
    "Cache eviction rate increased",
    "Request rate approaching configured limit",
    "Temporary connection failure",
    "Background job delayed",
    "Consumer lag is increasing",
    "Response payload larger than expected",
    "Disk I/O latency increased",
    "Memory allocation rate increased",
    "CPU utilization above normal",
    "Rate limit threshold approaching",
]

ERROR_MESSAGES = [
    "Database timeout",
    "Redis connection lost",
    "Unhandled exception",
    "Payment gateway unavailable",
    "Authentication failed",
    "Service unavailable",
    "Failed to write file",
    "Out of memory while processing request",
    "Database connection refused",
    "Failed to process request",
    "Message processing failed",
    "Unable to reach external service",
    "Connection reset by peer",
    "Request processing timeout",
    "Failed to deserialize message",
    "Invalid configuration detected",
    "Worker crashed while processing task",
    "Failed to publish message",
    "Database transaction rolled back",
    "Unexpected response from upstream service",
    "Failed to acquire database connection",
    "Queue consumer disconnected",
    "Internal server error",
    "Failed to refresh access token",
    "File system operation failed",
    "Upstream service returned 503",
    "Request rejected by downstream service",
    "Circuit breaker opened",
    "Database transaction deadlock",
    "Message queue unavailable",
]

LOGS_PER_SECOND = 500
BATCH_SIZE = 100

MIN_SPIKE_PROBABILITY = 0.01
MAX_SPIKE_PROBABILITY = 0.05


def timestamp():
    return datetime.now(timezone.utc).isoformat(timespec="milliseconds")


def generate_normal_log():
    x = random.randrange(100)

    if x < 75:
        level = "INFO"
        message = random.choice(INFO_MESSAGES)
    elif x < 95:
        level = "WARN"
        message = random.choice(WARN_MESSAGES)
    else:
        level = "ERROR"
        message = random.choice(ERROR_MESSAGES)

    return f"{timestamp()} {level} {message}\n"


def generate_error_spike():
    count = random.randint(20, 49)

    return "".join(
        f"{timestamp()} ERROR {random.choice(ERROR_MESSAGES)}\n" for _ in range(count)
    )


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("file")
    args = parser.parse_args()

    batch_interval = BATCH_SIZE / LOGS_PER_SECOND

    with open(args.file, "a", buffering=1024 * 1024) as file:
        next_write = time.perf_counter()

        while True:
            logs = "".join(generate_normal_log() for _ in range(BATCH_SIZE))

            file.write(logs)

            # Randomize anomaly probability for every batch.
            spike_probability = random.uniform(
                MIN_SPIKE_PROBABILITY,
                MAX_SPIKE_PROBABILITY,
            )

            if random.random() < spike_probability:
                file.write(generate_error_spike())

            file.flush()

            next_write += batch_interval

            remaining = next_write - time.perf_counter()

            if remaining > 0:
                time.sleep(remaining)


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        pass
