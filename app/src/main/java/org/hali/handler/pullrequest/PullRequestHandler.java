package org.hali.handler.pullrequest;

import java.util.function.Consumer;
import org.hali.functional.ConsumerHandler;
import org.hali.handler.model.RepositoryInfo;
import org.springframework.stereotype.Component;

@Component
public class PullRequestHandler implements ConsumerHandler<RepositoryInfo> {

    @Override
    public Consumer<RepositoryInfo> consumer() {
        return repositoryInfo -> {

        };
    }
}
