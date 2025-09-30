package org.hali.logging;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ThreadContextLogger implements EventStepLogger {

    @Override
    public void logInfo(String event, String step, String status, String message) {
        log(event, step, status, null, () -> log.info(message));
    }

    @Override
    public void logError(String event, String step, String status, String reason, String message, Exception ex) {
        log(event, step, status, reason, () -> log.error(message, ex));
    }

    private void log(String event, String step, String status, String reason, Runnable runnable) {
        try {
            ThreadContext.put("event", event);
            ThreadContext.put("step", step);
            ThreadContext.put("status", status);
            if (reason != null) {
                ThreadContext.put("reason", reason);
            }

            runnable.run();
        } catch (Exception e) {
            ThreadContext.remove("event");
            ThreadContext.remove("step");
            ThreadContext.remove("status");
            ThreadContext.remove("reason");
        }
    }
}

