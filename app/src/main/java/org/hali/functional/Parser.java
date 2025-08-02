package org.hali.functional;

import org.hali.exception.GithubEventContextParsingException;
import org.yaml.snakeyaml.parser.ParserException;

public interface Parser<T, R, O> {
    O parse(T t, R r) throws ParserException, GithubEventContextParsingException;
}
