package org.hali.http.extractor;

import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class HttpHeaderExtractor implements HeaderExtractor {

    @Override
    public Optional<String> extract(Map<String, String> headers, String name) {
        return Optional.ofNullable(headers.get(name.toLowerCase()));
    }
}
