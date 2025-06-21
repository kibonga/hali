package org.hali.handler.webhook.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.hali.handler.model.RepositoryInfo;
import org.hali.json.JsonParser;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public abstract class RepositoryInfoParser implements JsonParser<RepositoryInfo> {

    @Override
    public RepositoryInfo parse(JsonNode input) {
        log.info(input.toString());
        override();
        return new RepositoryInfo();
    }

    protected abstract void override();
}
