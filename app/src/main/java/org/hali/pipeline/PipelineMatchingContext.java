package org.hali.pipeline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PipelineMatchingContext {
    // eg. branches, pull-requests
    private final PipelineTriggerType pipelineTriggerType;

    // eg. master, uat, hotfix, **
    private final PipelineRefPattern pipelineRefPattern;
}
