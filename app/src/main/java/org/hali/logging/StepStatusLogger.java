package org.hali.logging;

public interface StepStatusLogger {
    void logInfo(StepStatus stepStatus, String message);
    void logError(StepStatus stepStatus, String reason, String message, Exception ex);
}
