package org.hali.pipeline;

import org.hali.common.model.GithubEventContext;

public interface PipelineMatchingContextBuilder {
    PipelineMatchingContext build(GithubEventContext githubEventContext);
}
