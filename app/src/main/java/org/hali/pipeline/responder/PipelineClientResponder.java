package org.hali.pipeline.responder;

import org.hali.http.responder.DefaultHttpClientResponder;
import org.springframework.stereotype.Component;

import java.net.http.HttpClient;

@Component
public class PipelineClientResponder extends DefaultHttpClientResponder {

    public PipelineClientResponder(HttpClient httpClient) {
        super(httpClient);
    }
}
