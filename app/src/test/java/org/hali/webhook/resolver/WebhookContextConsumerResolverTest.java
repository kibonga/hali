package org.hali.webhook.resolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.function.Consumer;
import org.hali.handler.event.pullrequest.PullRequestHandler;
import org.hali.handler.event.push.PushHandler;
import org.hali.handler.webhook.domain.WebhookContext;
import org.hali.handler.webhook.resolver.WebhookContextConsumerResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WebhookContextConsumerResolverTest {

    @Mock
    private PullRequestHandler pullRequestHandler;
    @Mock
    private PushHandler pushHandler;
    @InjectMocks
    private WebhookContextConsumerResolver resolver;

    @Test
    void resolve_validHandlerTypePassed_returnsGitHubEventContextConsumer() {
        // Arrange
        final Consumer<WebhookContext> pushConsumer = mock(Consumer.class);
        final Consumer<WebhookContext> pullRequestConsumer =
            mock(Consumer.class);

        when(this.pushHandler.consumer()).thenReturn(pushConsumer);
        when(this.pullRequestHandler.consumer()).thenReturn(
            pullRequestConsumer);

        // Act
        this.resolver.init();
        final var pushConsumerResult = this.resolver.resolve("push");
        final var pullRequestHandlerResult =
            this.resolver.resolve("pull_request");

        // Assert
        verify(this.pushHandler, times(1)).consumer();
        verify(this.pullRequestHandler, times(1)).consumer();
        // TODO - check this test
//        assertEquals(pushConsumer, pushConsumerResult);
//        assertEquals(pullRequestConsumer, pullRequestHandlerResult);
    }

    @Test
    void resolve_invalidHandlerTypePassed_returnsNull() {
        // Arrange
        final Consumer<WebhookContext> pushConsumer = mock(Consumer.class);
        final Consumer<WebhookContext> pullRequestConsumer =
            mock(Consumer.class);

        when(this.pushHandler.consumer()).thenReturn(pushConsumer);
        when(this.pullRequestHandler.consumer()).thenReturn(
            pullRequestConsumer);

        // Act
        this.resolver.init();
        final var invalidGitHubEventContextConsumer =
            this.resolver.resolve("invalidGitHubEventContext");

        // Assert
        verify(this.pushHandler, times(1)).consumer();
        verify(this.pullRequestHandler, times(1)).consumer();
        assertEquals(invalidGitHubEventContextConsumer, Optional.empty());
    }
}
