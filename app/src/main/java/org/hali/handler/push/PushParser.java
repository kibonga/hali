package org.hali.handler.push;

import com.fasterxml.jackson.databind.JsonNode;
import org.hali.handler.webhook.parser.GithubEventContextParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Qualifier("push_parser")
public class PushParser extends GithubEventContextParser {

    private static final String SEPARATOR = "refs/heads";

    private static final String REF = "ref";
    private static final String COMMIT_HASH = "after";

    @Override
    protected Function<JsonNode, String> commitShaFn() {
        return node -> getStringAsPath(node, COMMIT_HASH);
    }

    @Override
    protected Function<JsonNode, String> branchFn() {
        return node -> getStringAsPath(node, REF).split(SEPARATOR)[1];
    }
}
