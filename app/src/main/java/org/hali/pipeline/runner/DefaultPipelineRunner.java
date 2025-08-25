package org.hali.pipeline.runner;

import lombok.RequiredArgsConstructor;
import org.hali.exception.PipelineExecutorException;
import org.hali.exception.PipelineRunnerException;
import org.hali.pipeline.executor.PipelineExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultPipelineRunner implements PipelineRunner {

    private static final Logger log = LoggerFactory.getLogger(DefaultPipelineRunner.class);
    private final PipelineExecutor pipelineExecutor;

    @Override
    public Boolean run(List<String> steps, Path path) throws PipelineRunnerException, IOException {

        for (String step : steps) {
            try {
                final int pipelineStepExecutionResult = this.pipelineExecutor.executePipelineStep(step, new File(path.toString()));

                if (pipelineStepExecutionResult != 0) {
                    this.pipelineExecutor.cleanUp();
                    return false;
                }
            } catch (PipelineExecutorException e) {
                log.error("Pipeline runner exception occurred running the pipeline step");
                return false;
//                throw new PipelineRunnerException(
//                    "Pipeline runner exception occurred running the pipeline step",
//                    step,
//                    path.toString(),
//                    e
//                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.pipelineExecutor.cleanUp();
        return true;
    }
}
