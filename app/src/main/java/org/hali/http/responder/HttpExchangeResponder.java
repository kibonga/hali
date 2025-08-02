package org.hali.http.responder;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public interface HttpExchangeResponder {
    void succes(HttpExchange httpExchange, int status, long length) throws IOException;

    void error(HttpExchange httpExchange, int status, String message) throws IOException;
}
