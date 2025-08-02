package org.hali.pipeline.responder;

import org.hali.common.model.BuildStatus;
import org.hali.exception.PipelineBuildStatusResponderException;

public interface PipelineBuildStatusResponder {
    void send(BuildStatus buildStatus, String url) throws PipelineBuildStatusResponderException;
}
