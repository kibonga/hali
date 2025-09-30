package org.hali.handler.webhook.resolver;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.hali.functional.ConsumerResolver;
import org.hali.handler.event.pullrequest.PullRequestHandler;
import org.hali.handler.event.push.PushHandler;
import org.hali.handler.webhook.domain.WebhookContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class WebhookContextConsumerResolver implements
    ConsumerResolver<WebhookContext> {

    private final PullRequestHandler pullRequestHandler;
    private final PushHandler pushHandler;

    private final Map<String, Consumer<WebhookContext>> consumers =
        new HashMap<>();

    @PostConstruct
    public void init() {
        this.consumers.put("push", this.pushHandler.consumer());
        this.consumers.put("pull_request", this.pullRequestHandler.consumer());
    }

    @Override
    public Optional<Consumer<WebhookContext>> resolve(String type) {
        return Optional.ofNullable(this.consumers.get(type));
    }
}
