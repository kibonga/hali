package org.hali.pipeline;

import java.util.List;
import java.util.Map;

public interface PipelineStepExtractor {
    List<String> extractSteps(Map<String, Object> pipeline, PipelineMatchingContext pipelineMatchingContext);
}
