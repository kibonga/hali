package org.hali.yaml;

import org.hali.exception.YamlParsingException;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Objects.isNull;

@Component
public class DefaultYamlParser implements YamlParser {

    private final Yaml yaml = new Yaml();

    @Override
    public Map<String, Object> parse(InputStream yamlInputStream) throws YamlParsingException {
        return parse(() -> this.yaml.load(yamlInputStream));
    }

    @Override
    public Map<String, Object> parse(String yamlString) throws YamlParsingException {
        return parse(() -> this.yaml.load(yamlString));
    }

    private static Map<String, Object> parse(Supplier<Map<String, Object>> supplier) throws YamlParsingException {
        final Map<String, Object> yaml = supplier.get();

        if (isNull(yaml)) {
            throw new YamlParsingException("Empty YAML provided");
        }

        return yaml;
    }
}
