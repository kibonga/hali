package org.hali.http.responder;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public interface HttpExchangeResponder {
    void succes(HttpExchange httpExchange) throws IOException;

    void error(HttpExchange httpExchange, int status, String message) throws IOException;
}
