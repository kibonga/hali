package org.hali.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounterFactory {

    private final MeterRegistry registry;

    public Counter createCounter(MetricInfo metricInfo) {
        final Counter.Builder builder = Counter.builder(metricInfo.name())
            .description(metricInfo.description());

        metricInfo.tags().forEach(builder::tags);

        return builder.register(this.registry);
    }
}
