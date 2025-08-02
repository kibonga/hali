package org.hali.pipeline;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DefaultPipelineStepExtractor implements PipelineStepExtractor {

    @Override
    public List<String> extractSteps(Map<String, Object> pipeline, PipelineMatchingContext pipelineMatchingContext) {
        return extractSteps(
            pipeline,
            "pipelines",
            pipelineMatchingContext.getPipelineTriggerType().name(),
            pipelineMatchingContext.getPipelineRefPattern().name()
        );
    }

    @SuppressWarnings("unchecked")
    private static List<String> extractSteps(Map<String, Object> pipeline, String... steps) {
        Object object = pipeline;

        for (final String step : steps) {
            if (!(object instanceof Map)) {
                return Collections.emptyList();
            }

            object = ((Map<?, ?>) object).get(step);
        }

        if (object instanceof LinkedHashMap<?, ?> map) {
            return (ArrayList<String>) map.firstEntry().getValue();
        }

        return Collections.emptyList();
    }
}
