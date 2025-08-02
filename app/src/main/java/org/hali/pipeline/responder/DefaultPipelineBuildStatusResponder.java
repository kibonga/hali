package org.hali.pipeline.responder;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.hali.common.model.BuildStatus;
import org.hali.common.serde.ObjectSerializer;
import org.hali.exception.HttpClientResponderException;
import org.hali.exception.PipelineBuildStatusResponderException;
import org.hali.http.responder.HttpClientResponder;
import org.hali.http.responder.HttpResponseContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DefaultPipelineBuildStatusResponder implements PipelineBuildStatusResponder {

    private String authToken = "token";

    private final HttpClientResponder httpClientResponder;
    private final ObjectSerializer<Map<String, Object>> objectSerializer;

    @Override
    public void send(BuildStatus buildStatus, String url) throws PipelineBuildStatusResponderException {
        final Map<String, Object> payload = Map.of(
            "state", buildStatus.state(),
            "context", buildStatus.context(),
            "description", buildStatus.description()
        );

        try {
            final byte[] bytes = this.objectSerializer.serialize(payload);

            final HttpResponseContext httpResponseContext = new HttpResponseContext(url, "POST", this.authToken, bytes);

            this.httpClientResponder.send(httpResponseContext);
        } catch (JsonProcessingException e) {
            throw new PipelineBuildStatusResponderException("Failed serializing pipeline response", e);
        }catch (URISyntaxException e) {
            throw new PipelineBuildStatusResponderException("Invalid URL for pipeline response", e);
        } catch (IOException | HttpClientResponderException e) {
            throw new PipelineBuildStatusResponderException("Failed sending pipeline response", e);
        }
    }
}
