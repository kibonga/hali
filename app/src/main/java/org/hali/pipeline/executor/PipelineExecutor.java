package org.hali.pipeline.executor;

import org.hali.exception.PipelineExecutorException;

import java.io.File;

public interface PipelineExecutor {
    int executePipelineStep(String step, File file) throws PipelineExecutorException;
}
