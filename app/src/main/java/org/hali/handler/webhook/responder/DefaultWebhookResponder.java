package org.hali.handler.webhook.responder;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class DefaultWebhookResponder implements WebhookResponder {

    @Override
    public ResponseEntity<Void> success() {
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<String> error(String message) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .header("Content-Type", "text/plain")
            .body(message);
    }
}
