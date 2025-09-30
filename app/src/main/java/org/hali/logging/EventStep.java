package org.hali.logging;

public record EventStep(String event, String step, String status, String reason) {
    public EventStep(String event, String step, String status) {
        this(event, step, status, null);
    }
}
