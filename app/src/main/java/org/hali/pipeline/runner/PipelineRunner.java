package org.hali.pipeline.runner;

import org.hali.exception.PipelineRunnerException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface PipelineRunner {
    Boolean run(List<String> steps, Path path) throws PipelineRunnerException, IOException;
}
