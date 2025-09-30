package org.hali.metrics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounterMetric {

    private final CounterFactory counterFactory;

    public void increment(MetricInfo metricInfo) {
        this.counterFactory.createCounter(metricInfo).increment();
    }
}
