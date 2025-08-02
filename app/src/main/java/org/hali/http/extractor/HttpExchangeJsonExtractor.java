package org.hali.http.extractor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hali.json.JacksonJsonExtractor;
import org.springframework.stereotype.Component;

@Component
public class HttpExchangeJsonExtractor extends JacksonJsonExtractor {

    public HttpExchangeJsonExtractor(ObjectMapper objectMapper) {
        super(objectMapper);
    }
}
