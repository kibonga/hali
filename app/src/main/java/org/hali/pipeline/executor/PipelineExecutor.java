package org.hali.pipeline.executor;

import org.hali.exception.PipelineExecutorException;

import java.io.File;
import java.io.IOException;

public interface PipelineExecutor {
    int executePipelineStep(String step, File file) throws PipelineExecutorException;

    void cleanUp() throws IOException;
}
