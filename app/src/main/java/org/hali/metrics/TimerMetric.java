package org.hali.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TimerMetric {
    private final TimerFactory timerFactory;
    private final MeterRegistry meterRegistry;

    public Timer getTimer(MetricInfo metricInfo) {
        return this.timerFactory.createTimer(metricInfo);
    }

    public Timer.Sample getTimer() {
        return Timer.start();
    }

    public void stopTimer(Timer.Sample timer, MetricInfo metricInfo) {
       timer.stop(this.meterRegistry.timer(metricInfo.name()));
    }
}
