package org.hali.handler.webhook;

import org.hali.exception.WebhookHandlerException;

import java.util.Map;

public interface WebhookHandler {
    void handle(Map<String, String> headers, String payload) throws WebhookHandlerException;
}
