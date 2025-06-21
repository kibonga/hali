package org.hali.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.hali.exception.ExtractingException;

public abstract class JacksonJsonExtractor implements JsonExtractor {

    @Override
    public JsonNode extract(InputStream input)
        throws ExtractingException {
        try {
            final var objectMapper = new ObjectMapper();
            return objectMapper.readTree(input);
        } catch (IOException e) {
            throw new ExtractingException("Failed to extract json from input stream", e);
        }
    }
}
