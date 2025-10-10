package org.hali.webhook.parser;

import static org.junit.jupiter.api.Assertions.*;

import org.hali.handler.webhook.parser.WebhookContextParser;
import org.hali.handler.webhook.parser.DefaultWebhookContextParserResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DefaultWebhookContextParserResolverTest {

    @Mock
    private WebhookContextParser pushParser;

    @Mock
    private WebhookContextParser pullParser;

    private DefaultWebhookContextParserResolver defaultWebhookEventContextParserResolver;

    @BeforeEach
    void setUp() {
        this.defaultWebhookEventContextParserResolver = new DefaultWebhookContextParserResolver(this.pushParser, this.pullParser);
    }

    @Test
    void resolve_validgitHubEventContextParser_shouldReturngitHubEventContextParser() {
        final var pushgitHubEventContextParserResolver = this.defaultWebhookEventContextParserResolver.resolve("push");
        assertNotNull(pushgitHubEventContextParserResolver);
    }

    @Test
    void resolve_invalidgitHubEventContextParser_shouldReturnNull() {
        final var invalidgitHubEventContextParserResolver = this.defaultWebhookEventContextParserResolver.resolve("invalid_parser");
        assertEquals(Optional.empty(), invalidgitHubEventContextParserResolver);
    }
}
