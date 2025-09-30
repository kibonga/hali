package org.hali.http.responder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hali.exception.HttpClientResponderException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RequiredArgsConstructor
@Slf4j
public abstract class DefaultHttpClientResponder implements HttpClientResponder {

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public void send(HttpResponseContext httpResponseContext) throws IOException, URISyntaxException, HttpClientResponderException {

        final HttpRequest httpRequest = HttpRequest.newBuilder()
            .header("Content-Type", "application/json")
            .uri(URI.create(httpResponseContext.url()))
            .POST(HttpRequest.BodyPublishers.ofByteArray(httpResponseContext.data()))
            .build();

        try {
            final var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            log.info("Successfully received HTTP response. Status: {}, Body length: {}", response.statusCode(), response.body());
        } catch (IOException e) {
            throw new HttpClientResponderException("An error occurred while sending the HTTP request", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpClientResponderException("Thread was interrupted while waiting for an HTTP response", e);
        }
    }
}
