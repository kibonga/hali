package org.hali.handler.push;

import java.util.function.Consumer;
import org.hali.functional.ConsumerHandler;
import org.hali.handler.model.RepositoryInfo;
import org.springframework.stereotype.Component;

@Component
public class PushHandler implements ConsumerHandler<RepositoryInfo> {

    @Override
    public Consumer<RepositoryInfo> consumer() {
        return repositoryInfo -> {

        };
    }
}
