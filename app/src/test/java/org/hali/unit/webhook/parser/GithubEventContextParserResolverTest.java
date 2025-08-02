package org.hali.unit.webhook.parser;

import static org.junit.jupiter.api.Assertions.*;

import org.hali.handler.webhook.parser.GithubEventContextParser;
import org.hali.handler.webhook.parser.GithubEventContextParserResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GithubEventContextParserResolverTest {

    @Mock
    private GithubEventContextParser pushParser;

    @Mock
    private GithubEventContextParser pullParser;

    private GithubEventContextParserResolver githubEventContextParserResolver;

    @BeforeEach
    void setUp() {
        this.githubEventContextParserResolver = new GithubEventContextParserResolver(this.pushParser, this.pullParser);
    }

    @Test
    void resolve_validgitHubEventContextParser_shouldReturngitHubEventContextParser() {
        final var pushgitHubEventContextParserResolver = this.githubEventContextParserResolver.resolve("push");
        assertNotNull(pushgitHubEventContextParserResolver);
    }

    @Test
    void resolve_invalidgitHubEventContextParser_shouldReturnNull() {
        final var invalidgitHubEventContextParserResolver = this.githubEventContextParserResolver.resolve("invalid_parser");
        assertNull(invalidgitHubEventContextParserResolver);
    }
}
