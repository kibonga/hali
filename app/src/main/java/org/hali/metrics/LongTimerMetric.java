package org.hali.metrics;

import io.micrometer.core.instrument.LongTaskTimer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LongTimerMetric {
    private final LongRunningTaskFactory longRunningTaskFactory;

    public LongTaskTimer getLongTaskTimer(MetricInfo metricInfo) {
        return this.longRunningTaskFactory.createLongTaskTimer(metricInfo);
    }
}
