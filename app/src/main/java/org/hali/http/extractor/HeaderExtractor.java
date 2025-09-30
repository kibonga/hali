package org.hali.http.extractor;

import java.util.Map;
import java.util.Optional;

public interface HeaderExtractor {

    Optional<String> extract(Map<String, String> headers, String name);
}
