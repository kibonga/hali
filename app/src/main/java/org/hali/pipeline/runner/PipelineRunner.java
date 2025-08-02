package org.hali.pipeline.runner;

import org.hali.exception.PipelineRunnerException;

import java.nio.file.Path;
import java.util.List;

public interface PipelineRunner {
    void run(List<String> steps, Path path) throws PipelineRunnerException;
}
