package org.hali.metrics;

import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Accessors(fluent = true)
public record MetricInfo(String name, String description,
                         Map<String, String> tags) {

    public MetricInfo withAdditionalTags(Map<String, String> additionalTags) {
        final Map<String, String> newTags = new HashMap<>(this.tags);
        newTags.putAll(additionalTags);

        return new MetricInfo(this.name, this.description, newTags);
    }

    public MetricInfo withAdditionalTag(String  name, String value) {
        final Map<String, String> newTags = new HashMap<>(this.tags);
        newTags.put(name, value);

        return new MetricInfo(this.name, this.description, newTags);
    }
}
