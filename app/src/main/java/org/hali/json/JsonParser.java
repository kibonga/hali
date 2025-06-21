package org.hali.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.hali.functional.Parser;

public interface JsonParser<T> extends Parser<JsonNode, T> {

}
