package org.hali.pipeline;

import org.hali.handler.webhook.domain.WebhookContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DefaultPipelineRefPatternResolver implements PipelineRefPatternResolver {

    private static final PipelineRefPattern DEFAULT_PIPELINE_REF_PATTERN = new PipelineRefPattern("**");

    private static final Map<String, PipelineRefPattern> PIPELINE_REF_PATTERNS = Map.of(
        "master", new PipelineRefPattern("master"),
        "uat", new PipelineRefPattern("uat"),
        "hotfix", new PipelineRefPattern("hotfix")
    );

    @Override
    public PipelineRefPattern resolve(WebhookContext webhookContext) {
        final String branch = webhookContext.getBranch();
        final String refType = branch.contains("/") ? branch.substring(0, branch.lastIndexOf('/')) : branch;

        return PIPELINE_REF_PATTERNS.getOrDefault(refType, DEFAULT_PIPELINE_REF_PATTERN);
    }


}
