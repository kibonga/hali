package org.hali.handler.webhook.responder;

import org.springframework.http.ResponseEntity;

public interface WebhookResponder {
    ResponseEntity<Void> success();

    ResponseEntity<String> error(String message);
}
