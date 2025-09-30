package org.hali.handler.event.pullrequest;

import lombok.RequiredArgsConstructor;
import org.hali.logging.StepStatus;
import org.hali.logging.StepStatusLogger;
import org.hali.logging.ThreadContextLogger;
import org.springframework.stereotype.Component;

@Component("pullRequestHandlerLogger")
@RequiredArgsConstructor
public class PullRequestHandlerLogger implements StepStatusLogger {

    private static final String event = "pull_request_handler";

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
