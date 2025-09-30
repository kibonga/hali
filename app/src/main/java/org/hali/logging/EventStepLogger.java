package org.hali.logging;

public interface EventStepLogger {
    void logInfo(String event, String step, String status, String message);

    void logError(String event, String step, String status, String reason, String message, Exception ex);
}
