package org.hali.unit.webhook;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import lombok.SneakyThrows;
import org.hali.exception.ExtractingException;
import org.hali.functional.ConsumerResolver;
import org.hali.common.model.GithubEventContext;
import org.hali.handler.webhook.WebhookHandler;
import org.hali.handler.webhook.parser.GithubEventContextParser;
import org.hali.http.extractor.HeaderExtractor;
import org.hali.http.responder.HttpExchangeResponder;
import org.hali.json.JsonExtractor;
import org.hali.json.JsonParserResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WebhookHandlerTest {

    private static final String GITHUB_EVENT = "pull_request";

    @Mock
    private HeaderExtractor headerExtractor;
    @Mock
    private HttpExchangeResponder httpExchangeResponder;
    @Mock
    private HttpExchange httpExchange;
    @Mock
    private ConsumerResolver<GithubEventContext> githubEventContextConsumerResolver;
    @Mock
    private JsonParserResolver<JsonNode, String, GithubEventContext> jsonParserResolver;
    @Mock
    private JsonExtractor jsonExtractor;
    @Mock
    private Executor executor;

    @InjectMocks
    private WebhookHandler webhookHandler;

    @SneakyThrows
    @Test
    void handle_githubEventHeaderNotPresent_shouldRespond400ServerError() {
        // Arrange
        final Headers headers = mock(Headers.class);
        when(this.httpExchange.getRequestHeaders()).thenReturn(headers);
        when(this.headerExtractor.extract(eq(headers), anyString())).thenReturn(
            null);
        doNothing().when(this.httpExchangeResponder)
            .error(eq(this.httpExchange), eq(400), anyString());

        // Act
        this.webhookHandler.handle(this.httpExchange);

        // Assert
        verify(this.httpExchange, times(1)).getRequestHeaders();
        verify(this.headerExtractor, times(1)).extract(any(Headers.class),
            anyString());
        verify(this.httpExchangeResponder, times(1)).error(
            eq(this.httpExchange), eq(400), anyString());
    }

    @SneakyThrows
    @Test
    void handle_gitHubEventContextConsumerNotFoundForGithubEvent_shouldRespond422ServerError() {
        // Arrange
        final Headers headers = mock(Headers.class);

        when(this.httpExchange.getRequestHeaders()).thenReturn(headers);
        when(this.headerExtractor.extract(eq(headers), anyString())).thenReturn(
            GITHUB_EVENT);
        when(this.githubEventContextConsumerResolver.resolve(
            GITHUB_EVENT)).thenReturn(null);
        doNothing().when(this.httpExchangeResponder)
            .error(eq(this.httpExchange), eq(422), anyString());

        // Act
        this.webhookHandler.handle(this.httpExchange);

        // Assert
        verify(this.httpExchange, times(1)).getRequestHeaders();
        verify(this.headerExtractor, times(1)).extract(any(Headers.class),
            anyString());
        verify(this.githubEventContextConsumerResolver, times(1)).resolve(
            GITHUB_EVENT);
        verify(this.httpExchangeResponder, times(1)).error(
            eq(this.httpExchange), eq(422), anyString());
    }

    @SneakyThrows
    @Test
    void handle_gitHubEventContextParserNotFoundForGithubEvent_shouldRespond422ServerError() {
        // Arrange
        final Headers headers = mock(Headers.class);
        final Consumer<GithubEventContext> gitHubEventContextConsumer =
            mock(Consumer.class);

        when(this.httpExchange.getRequestHeaders()).thenReturn(headers);
        when(this.headerExtractor.extract(eq(headers), anyString())).thenReturn(
            GITHUB_EVENT);
        when(this.githubEventContextConsumerResolver.resolve(
            GITHUB_EVENT)).thenReturn(gitHubEventContextConsumer);
        when(this.jsonParserResolver.resolve(GITHUB_EVENT)).thenReturn(null);
        doNothing().when(this.httpExchangeResponder)
            .error(eq(this.httpExchange), eq(422), anyString());

        // Act
        this.webhookHandler.handle(this.httpExchange);

        // Assert
        verify(this.httpExchange, times(1)).getRequestHeaders();
        verify(this.headerExtractor, times(1)).extract(any(Headers.class),
            anyString());
        verify(this.githubEventContextConsumerResolver, times(1)).resolve(
            GITHUB_EVENT);
        verify(this.jsonParserResolver, times(1)).resolve(GITHUB_EVENT);
        verify(this.httpExchangeResponder, times(1)).error(
            eq(this.httpExchange), eq(422), anyString());
    }

    @SneakyThrows
    @Test
    void handle_failedExtractingWebhookPayload_shouldRespond422ServerError() {
        // Arrange
        final Headers headers = mock(Headers.class);
        final Consumer<GithubEventContext> gitHubEventContextConsumer =
            mock(Consumer.class);
        final GithubEventContextParser githubEventContextParser =
            mock(GithubEventContextParser.class);
        final InputStream requestBody = mock(InputStream.class);

        when(this.httpExchange.getRequestHeaders()).thenReturn(headers);
        when(this.headerExtractor.extract(eq(headers), anyString())).thenReturn(
            GITHUB_EVENT);
        when(this.githubEventContextConsumerResolver.resolve(
            GITHUB_EVENT)).thenReturn(gitHubEventContextConsumer);
        when(this.jsonParserResolver.resolve(GITHUB_EVENT)).thenReturn(
            githubEventContextParser);
        when(this.httpExchange.getRequestBody()).thenReturn(requestBody);
        doThrow(ExtractingException.class).when(this.jsonExtractor)
            .extract(requestBody);
        doNothing().when(this.httpExchangeResponder)
            .error(any(), eq(423), eq("Invalid payload provided"));

        // Act
        this.webhookHandler.handle(this.httpExchange);

        // Assert
        verify(this.httpExchange, times(1)).getRequestHeaders();
        verify(this.headerExtractor, times(1)).extract(any(Headers.class),
            anyString());
        verify(this.githubEventContextConsumerResolver, times(1)).resolve(
            GITHUB_EVENT);
        verify(this.jsonParserResolver, times(1)).resolve(GITHUB_EVENT);
        verify(this.httpExchange, times(1)).getRequestBody();
        verify(this.jsonExtractor, times(1)).extract(requestBody);
        verify(this.httpExchangeResponder, times(1)).error(
            any(), eq(423), eq("Invalid payload provided"));
    }

    @SneakyThrows
    @Test
    void handle_validHttpExchangePassed_shouldRespond200Success() {
        // Arrange
        final Headers headers = mock(Headers.class);
        final Consumer<GithubEventContext> gitHubEventContextConsumer =
            mock(Consumer.class);
        final GithubEventContextParser githubEventContextParser =
            mock(GithubEventContextParser.class);
        final InputStream requestBody = mock(InputStream.class);
        final JsonNode jsonNode = mock(JsonNode.class);
        final GithubEventContext githubEventContext = mock(GithubEventContext.class);

        when(this.httpExchange.getRequestHeaders()).thenReturn(headers);
        when(this.headerExtractor.extract(eq(headers), anyString())).thenReturn(
            GITHUB_EVENT);
        when(this.githubEventContextConsumerResolver.resolve(
            GITHUB_EVENT)).thenReturn(gitHubEventContextConsumer);
        when(this.jsonParserResolver.resolve(GITHUB_EVENT)).thenReturn(
            githubEventContextParser);
        when(this.httpExchange.getRequestBody()).thenReturn(requestBody);
        when(this.jsonExtractor.extract(requestBody)).thenReturn(jsonNode);
        when(githubEventContextParser.parse(jsonNode, "pull_request")).thenReturn(githubEventContext);
        doNothing().when(this.executor).execute(any(Runnable.class));
        doNothing().when(this.httpExchangeResponder)
            .succes(this.httpExchange, 200, 0);

        // Act
        this.webhookHandler.handle(this.httpExchange);

        // Assert
        verify(this.httpExchange, times(1)).getRequestHeaders();
        verify(this.headerExtractor, times(1)).extract(any(Headers.class),
            anyString());
        verify(this.githubEventContextConsumerResolver, times(1)).resolve(
            GITHUB_EVENT);
        verify(this.jsonParserResolver, times(1)).resolve(GITHUB_EVENT);
        verify(this.httpExchange, times(1)).getRequestBody();
        verify(this.jsonExtractor, times(1)).extract(requestBody);
        verify(githubEventContextParser, times(1)).parse(jsonNode, "pull_request");
        verify(this.executor).execute(any(Runnable.class));
        verify(this.httpExchangeResponder, times(1)).succes(this.httpExchange,
            200, 0);
    }
}
