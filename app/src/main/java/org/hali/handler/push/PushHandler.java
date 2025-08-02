package org.hali.handler.push;

import java.util.function.Consumer;
import org.hali.functional.ConsumerHandler;
import org.hali.common.model.GithubEventContext;
import org.springframework.stereotype.Component;

@Component
public class PushHandler implements ConsumerHandler<GithubEventContext> {

    @Override
    public Consumer<GithubEventContext> consumer() {
        return githubEventContext -> {

        };
    }
}
