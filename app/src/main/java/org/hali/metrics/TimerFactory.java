package org.hali.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TimerFactory {

    private final MeterRegistry registry;

    public Timer createTimer(MetricInfo metricInfo) {
        final Timer.Builder builder = Timer.builder(metricInfo.name())
            .description(metricInfo.description());

        metricInfo.tags().forEach(builder::tags);

        return builder.register(this.registry);
    }
}
