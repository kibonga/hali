package org.hali.handler.webhook.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookContext {
    WebhookType eventType;

    // These fields have different paths

    // Push - "after"
    // Pull_request - "head.sha"
    String commitHash;

    // Push - "ref" + parse()
    // Pull_request - "head.ref"
    String branch;


    // These fields have same paths

    // Push/Pull_request - "repository.full_name"
    String repositoryName;

    // Push/Pull_request - "repository.clone_url"
    String repoUrl;

    // Push/Pull_request - "repository.name"
    String projectName; // both push("repository.name"), pull_request("repository.name")

    // Push/Pull_request - apiUrlBase + repository_name + statuses + commit_hash
    String buildCheckUrl; // both push and pull_request (apiUrlBase + repository_name + statuses + commit_hash)
}
