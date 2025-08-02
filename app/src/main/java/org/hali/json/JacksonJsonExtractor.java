package org.hali.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.hali.exception.ExtractingException;

@RequiredArgsConstructor
public abstract class JacksonJsonExtractor implements JsonExtractor {

    private final ObjectMapper objectMapper;

    @Override
    public JsonNode extract(InputStream input) throws ExtractingException {
        try {
            final JsonNode jsonNode = this.objectMapper.readTree(input);

            if (jsonNode.isNull()) {
                throw new ExtractingException(
                    "Failed to extract json from input stream");
            }

            return jsonNode;
        } catch (IOException e) {
            throw new ExtractingException(
                "Failed to extract json from input stream", e);
        }
    }
}
