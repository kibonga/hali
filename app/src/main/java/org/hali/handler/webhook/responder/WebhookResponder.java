package org.hali.handler.webhook.responder;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import org.hali.http.responder.HttpExchangeResponder;
import org.springframework.stereotype.Component;

@Component
public class WebhookResponder implements HttpExchangeResponder {

    @Override
    public void succes(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
    }

    @Override
    public void error(HttpExchange httpExchange, int status, String message)
        throws IOException {
        httpExchange.getResponseHeaders().add("Content-Type", "text/plain");
        httpExchange.sendResponseHeaders(status, message.length());

        try (final OutputStream os = httpExchange.getResponseBody()) {
            os.write(message.getBytes());
        }
    }
}
