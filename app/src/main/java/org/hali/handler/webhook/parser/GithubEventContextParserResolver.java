package org.hali.handler.webhook.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.hali.common.model.GithubEventContext;
import org.hali.functional.Parser;
import org.hali.json.JsonParserResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GithubEventContextParserResolver implements
    JsonParserResolver<JsonNode, String, GithubEventContext> {

    private final Map<String, GithubEventContextParser> parsers = new HashMap<>();

    @Autowired
    public GithubEventContextParserResolver(
        @Qualifier("push_parser") Parser pushParser,
        @Qualifier("pull_request_parser") Parser pullRequestParser) {
        this.parsers.put("push", (GithubEventContextParser) pushParser);
        this.parsers.put("pull_request",
            (GithubEventContextParser) pullRequestParser);
    }

    @Override
    public GithubEventContextParser resolve(String type) {
        return this.parsers.get(type);
    }
}
