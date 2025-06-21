package org.hali.handler.webhook.parser;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;
import org.hali.handler.model.RepositoryInfo;
import org.hali.json.JsonParserResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RepositoryInfoParserResolver implements
    JsonParserResolver<JsonNode, RepositoryInfo> {

    private final Map<String, RepositoryInfoParser> parsers =
        new HashMap<>();

    @Autowired
    public RepositoryInfoParserResolver(
        @Qualifier("push_parser") RepositoryInfoParser pushParser,
        @Qualifier("pull_request_parser") RepositoryInfoParser pullRequestParser) {
        this.parsers.put("push", pushParser);
        this.parsers.put("pull_request", pullRequestParser);
    }

    @Override
    public RepositoryInfoParser resolve(String type) {
        return this.parsers.get(type);
    }
}
