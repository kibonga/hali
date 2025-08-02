package org.hali.handler.webhook.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.hali.common.model.GithubEventContext;
import org.hali.common.model.GithubEventType;
import org.hali.exception.GithubEventContextParsingException;
import org.hali.json.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
@Slf4j
public abstract class GithubEventContextParser implements JsonParser<GithubEventContext> {

    protected static final String STATUSES = "/statuses/";
    protected static final String REPOSITORY = "repository";

    private static final String[] REPOSITORY_NAME_PATH =
        new String[]{REPOSITORY, "full_name"};
    private static final String[] REPO_URL_PATH =
        new String[]{REPOSITORY, "clone_url"};
    private static final String[] PROJECT_NAME_PATH =
        new String[]{REPOSITORY, "name"};

    private static final Map<String, GithubEventType> githubEventTypes = Map.of(
        "pull_request", GithubEventType.PULL_REQUEST,
        "push", GithubEventType.PUSH
    );

    @Value("${api.url-base}")
    private String apiUrlBase;

    @Override
    public GithubEventContext parse(JsonNode node, String githubEvent) throws GithubEventContextParsingException {
        final GithubEventContext githubEventContext = new GithubEventContext();

        extractCommonFields(node, githubEventContext);
        extractCustomFields(node, githubEventContext);

        final GithubEventType githubEventType = githubEventTypes.get(githubEvent);

        if (Objects.isNull(githubEventType)) {
            throw new GithubEventContextParsingException("Invalid GitHub event type provided: " + githubEvent);
        }

        githubEventContext.setEventType(githubEventType);

        // Specific field - "build check url"
        githubEventContext.setBuildCheckUrl(this.apiUrlBase + githubEventContext.getRepositoryName() + STATUSES + githubEventContext.getCommitHash());

        return githubEventContext;
    }

    private void extractCustomFields(JsonNode node, GithubEventContext githubEventContext) {
        githubEventContext.setCommitHash(commitShaFn().apply(node));
        githubEventContext.setBranch(branchFn().apply(node));
    }

    private static void extractCommonFields(JsonNode node, GithubEventContext githubEventContext) {
        githubEventContext.setRepositoryName(repositoryNameFn.apply(node));
        githubEventContext.setRepoUrl(repoUrlFn.apply(node));
        githubEventContext.setProjectName(projectNameFn.apply(node));
    }

    protected static String getStringAsPath(JsonNode root, String... path) {
        JsonNode node = root;

        for (final String key : path) {
            node = node.path(key);
        }

        return node.asText();
    }

    private static final Function<JsonNode, String> repositoryNameFn = node -> getStringAsPath(node, REPOSITORY_NAME_PATH);
    private static final Function<JsonNode, String> repoUrlFn = node -> getStringAsPath(node, REPO_URL_PATH);
    private static final Function<JsonNode, String> projectNameFn = node -> getStringAsPath(node, PROJECT_NAME_PATH);

    protected abstract Function<JsonNode, String> commitShaFn();

    protected abstract Function<JsonNode, String> branchFn();
}
