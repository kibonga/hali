package org.hali.pipeline;

import org.hali.common.model.GithubEventContext;
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
    public PipelineRefPattern resolve(GithubEventContext githubEventContext) {
        final String branch = githubEventContext.getBranch();
        final String refType = branch.contains("/") ? branch.substring(0, branch.lastIndexOf('/')) : branch;

        return PIPELINE_REF_PATTERNS.getOrDefault(refType, DEFAULT_PIPELINE_REF_PATTERN);
    }


}
