package org.hali.metrics;

import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LongRunningTaskFactory {

    private final MeterRegistry registry;

    public LongTaskTimer createLongTaskTimer(MetricInfo metricInfo) {
        final LongTaskTimer.Builder builder = LongTaskTimer.builder(metricInfo.name())
            .description(metricInfo.description());

        metricInfo.tags().forEach(builder::tags);

        return builder.register(this.registry);
    }
}
