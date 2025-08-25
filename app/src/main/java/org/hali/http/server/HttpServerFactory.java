package org.hali.http.server;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;

public interface HttpServerFactory {
HttpServer create(String host, int port) throws IOException;
}
