package com.decipher.anomalog.model;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LogEntry(
        Instant timestamp,
        @NotNull LogLevel level,
        @NotBlank String message) {
}
