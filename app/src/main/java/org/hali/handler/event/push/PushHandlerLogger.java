package org.hali.handler.event.push;

import lombok.RequiredArgsConstructor;
import org.hali.logging.StepStatus;
import org.hali.logging.StepStatusLogger;
import org.hali.logging.ThreadContextLogger;
import org.springframework.stereotype.Component;

@Component("pushHandlerLogger")
@RequiredArgsConstructor
public class PushHandlerLogger implements StepStatusLogger {

    private static final String event = "push_handler";

    private final ThreadContextLogger threadContextLogger;

    @Override
    public void logInfo(StepStatus stepStatus, String message) {
        this.threadContextLogger.logInfo(event, stepStatus.step(), stepStatus.status(), message);
    }

    @Override
    public void logError(StepStatus stepStatus, String reason, String message, Exception ex) {
        this.threadContextLogger.logError(event, stepStatus.step(), stepStatus.status(), reason, message, ex);
    }
}
