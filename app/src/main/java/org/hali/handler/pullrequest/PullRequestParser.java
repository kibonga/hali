package org.hali.handler.pullrequest;

import com.fasterxml.jackson.databind.JsonNode;
import org.hali.handler.webhook.parser.GithubEventContextParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Qualifier("pull_request_parser")
public class PullRequestParser extends GithubEventContextParser {

    private static final String PULL_REQUEST = "pull_request";
    private static final String HEAD = "head";

    private static final String[] COMMIT_HASH =
        new String[]{PULL_REQUEST, HEAD, "sha"};
    private static final String[] BRANCH =
        new String[]{PULL_REQUEST, HEAD, "ref"};

    @Override
    protected Function<JsonNode, String> commitShaFn() {
        return node -> getStringAsPath(node, COMMIT_HASH);
    }

    @Override
    protected Function<JsonNode, String> branchFn() {
        return node -> getStringAsPath(node, BRANCH);
    }
}
