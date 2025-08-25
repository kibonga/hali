package org.hali.pipeline;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class DefaultPipelineStepExtractor implements PipelineStepExtractor {

    @Override
    public List<String> extractSteps(Map<String, Object> pipelines, PipelineMatchingContext pipelineMatchingContext) {
        final var triggerTypeMap = extractTriggerTypeMap(pipelines, pipelineMatchingContext.getPipelineTriggerType().name());

        if (isNull(triggerTypeMap) || triggerTypeMap.isEmpty()) {
            return Collections.emptyList();
        }

        final var steps = extractRefPattern(triggerTypeMap, pipelineMatchingContext.getPipelineRefPattern().name());

        return steps.orElse(Collections.emptyList());

    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> extractTriggerTypeMap(Map<String, Object> pipelines, String triggerType) {
        return (Map<String, Object>) pipelines.get(triggerType);
    }

    @SuppressWarnings("unchecked")
    private static Optional<List<String>> extractRefPattern(Map<String, Object> triggerType, String refPattern) {
        return Optional.ofNullable(triggerType.get(refPattern))
            .or(() -> Optional.ofNullable(triggerType.get("**")))
            .map(object ->
                (List<String>) ((LinkedHashMap<?, ?>) object).firstEntry().getValue()
            );
    }

//    @SuppressWarnings("unchecked")
//    private static List<String> extractSteps(Map<String, Object> pipeline, String... steps) {
//        Object object = pipeline;
//
//        for (final String step : steps) {
//            if (!(object instanceof Map)) {
//                return Collections.emptyList();
//            }
//
//            object = ((Map<?, ?>) object).get(step);
//        }
//
//        if (object instanceof LinkedHashMap<?, ?> map) {
//            return (ArrayList<String>) map.firstEntry().getValue();
//        }
//
//        return Collections.emptyList();
//    }
}
