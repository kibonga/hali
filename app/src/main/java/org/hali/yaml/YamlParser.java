package org.hali.yaml;

import org.hali.exception.YamlParsingException;

import java.io.InputStream;
import java.util.Map;

public interface YamlParser {
    Map<String, Object> parse(InputStream yamlInputStream) throws YamlParsingException;
    Map<String, Object> parse(String yamlString) throws YamlParsingException;
}
