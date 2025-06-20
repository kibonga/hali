package org.hali.http.server;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.springframework.stereotype.Component;

@Component
public class DefaultHttpServerFactory implements HttpServerFactory {

    @Override
    public HttpServer create(String host, int port) throws IOException {
        return HttpServer.create(new InetSocketAddress(host, port), 0);
    }
}
