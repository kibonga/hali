package org.hali.handler.webhook.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hali.handler.webhook.responder.WebhookResponder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/webhook/handler")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookResponder webhookResponder;

    @PostMapping("/pipeline")
    public ResponseEntity<?> handlePipeline(@RequestHeader Map<String, String> header, @RequestBody String payload) {
        log.info("Received webhook payload: {}", payload);

        final double random = Math.random() * 2;
        if (random > 5) {
            return this.webhookResponder.error("Error");
        }

        return this.webhookResponder.success();
    }
}
