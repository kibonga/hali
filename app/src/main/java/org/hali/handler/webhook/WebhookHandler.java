package org.hali.handler.webhook;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class WebhookHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final var messsage = "Hello from Hali!";
        exchange.sendResponseHeaders(200, messsage.length());
        exchange.getResponseBody().write(messsage.getBytes());
    }
}
