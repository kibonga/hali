package org.hali.handler.webhook.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.hali.handler.webhook.domain.WebhookType;
import org.hali.handler.webhook.domain.WebhookContext;
import org.hali.json.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Component
@Slf4j
public abstract class WebhookContextParser implements JsonParser<WebhookContext> {

    protected static final String STATUSES = "/statuses/";
    protected static final String REPOSITORY = "repository";

    private static final String[] REPOSITORY_NAME_PATH =
        new String[]{REPOSITORY, "full_name"};
    private static final String[] REPO_URL_PATH =
        new String[]{REPOSITORY, "clone_url"};
    private static final String[] PROJECT_NAME_PATH =
        new String[]{REPOSITORY, "name"};

    private static final Map<String, WebhookType> githubEventTypes = Map.of(
        "pull_request", WebhookType.PULL_REQUEST,
        "push", WebhookType.PUSH
    );

    public WebhookContextParser() {
    }

    @Override
    public Optional<WebhookContext> parse(JsonNode node, String webhookEvent) {
        final WebhookType webhookType = githubEventTypes.get(webhookEvent);

        if (Objects.isNull(webhookType)) {
            log.error("Invalid webhook event type: {}", webhookEvent);

            return Optional.empty();
        }

        final var optionalApiUrlBase = Optional.ofNullable(System.getProperty("api.url-base"));
        if (optionalApiUrlBase.isEmpty()) {
            log.error("Api URL base is not defined");
            return Optional.empty();
        }

        final WebhookContext webhookContext = new WebhookContext();

        extractCommonFields(node, webhookContext);
        extractCustomFields(node, webhookContext);

        webhookContext.setEventType(webhookType);

        // Specific field - "build check url"
        webhookContext.setBuildCheckUrl(optionalApiUrlBase.get() + "/" + webhookContext.getRepositoryName() + STATUSES + webhookContext.getCommitHash());

        return Optional.of(webhookContext);
    }

    private void extractCustomFields(JsonNode node, WebhookContext webhookContext) {
        webhookContext.setCommitHash(commitShaFn().apply(node));
        webhookContext.setBranch(branchFn().apply(node));
    }

    private static void extractCommonFields(JsonNode node, WebhookContext webhookContext) {
        webhookContext.setRepositoryName(repositoryNameFn.apply(node));
        webhookContext.setRepoUrl(repoUrlFn.apply(node));
        webhookContext.setProjectName(projectNameFn.apply(node));
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
