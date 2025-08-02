package org.hali.http.responder;

import org.hali.exception.HttpClientResponderException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface HttpClientResponder {
    void send(HttpResponseContext httpResponseContext) throws IOException, URISyntaxException, HttpClientResponderException;
}
