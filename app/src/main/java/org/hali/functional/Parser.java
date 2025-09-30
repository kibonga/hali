package org.hali.functional;

import org.hali.exception.WebhookEventContextParsingException;
import org.yaml.snakeyaml.parser.ParserException;

import java.util.Optional;

public interface Parser<T, R, O> {
    Optional<O> parse(T t, R r);
}
