package org.hali.json;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.InputStream;
import org.hali.functional.Extractor;

public interface JsonExtractor extends Extractor<InputStream, JsonNode> {
}
