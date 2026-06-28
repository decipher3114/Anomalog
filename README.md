# Log Aggregator

A Spring Boot log aggregation service that reads a shared `app.log` file, parses incoming entries, tracks log metrics, and detects error spikes in near real time.

## What it does

The aggregator is designed to be the central processing service for a companion log emitter.

It:
- tails the shared `app.log` file
- parses each log line into timestamp, level, and message
- batches new log entries every second
- tracks total log counts and counts per log level
- detects error spikes using a sliding window of recent batches
- publishes WebSocket events for processed batches, metrics snapshots, and anomalies
- exposes REST endpoints for metrics and anomalies

## Companion emitter

The emitter is a simple standalone Java app that writes synthetic log lines into the shared `app.log` file.

It:
- writes regular INFO, WARN, and ERROR logs
- occasionally emits a burst of ERROR logs
- is meant to simulate a noisy application so the aggregator has real input to process

## How the pieces fit together

Both apps read and write the same shared file:

- the emitter appends new log lines to `app.log`
- the aggregator tails that file and ingests the new lines
- the aggregator groups logs into batches and updates metrics
- if error volume spikes, the aggregator raises an anomaly event

## Requirements

- Java 21
- Maven 3.9 or newer

## Configuration

Aggregator defaults are defined in `src/main/resources/application.properties`:

- log source path: `../app.log`
- polling interval: `200ms`
- batch processing: every `1000ms`
- anomaly window size: `5`
- anomaly history size: `100`

## Running the aggregator

From the repo directory:

```bash
cd log-aggregator
mvn spring-boot:run
```

## Running the emitter
From the repo directory:
```bash
cd log-emitter
mvn exec:java
```

The emitter writes to the shared `app.log` file in the parent directory.

How to test it
1. Start the emitter first.
2. Start the aggregator in a second terminal.
3. Wait a few seconds for logs to accumulate.
4. Check the REST endpoints:
  - `GET http://localhost:8080/metrics`
  - `GET http://localhost:8080/anomalies`
5. Connect a WebSocket client to:
  - `ws://localhost:8080/ws/events`
6. Watch for events such as:
  - `BATCH_PROCESSED`
  - `METRICS_SNAPSHOT`
  - `ANOMALY_DETECTED`
7. Let it run until an error spike occurs, then confirm an anomaly appears in `/anomalies`.

# Notes
The aggregator expects each log line to use the format:
- ISO-8601 timestamp
- log level
- message
Example:
- `2026-06-26T12:00:00Z ERROR Database timeout`
