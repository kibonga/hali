package org.hali.handler.webhook.resolver;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.hali.functional.ConsumerResolver;
import org.hali.common.model.GithubEventContext;
import org.hali.handler.pullrequest.PullRequestHandler;
import org.hali.handler.push.PushHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GithubEventContextConsumerResolver implements
    ConsumerResolver<GithubEventContext> {

    private final PullRequestHandler pullRequestHandler;
    private final PushHandler pushHandler;

    private final Map<String, Consumer<GithubEventContext>> consumers =
        new HashMap<>();

    @PostConstruct
    public void init() {
        this.consumers.put("push", this.pushHandler.consumer());
        this.consumers.put("pull_request", this.pullRequestHandler.consumer());
    }

    @Override
    public Consumer<GithubEventContext> resolve(String type) {
        return this.consumers.get(type);
    }
}
