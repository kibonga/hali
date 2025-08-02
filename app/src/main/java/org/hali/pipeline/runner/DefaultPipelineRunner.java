package org.hali.pipeline.runner;

import lombok.RequiredArgsConstructor;
import org.hali.exception.PipelineExecutorException;
import org.hali.exception.PipelineRunnerException;
import org.hali.pipeline.executor.PipelineExecutor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultPipelineRunner implements PipelineRunner {

    private final PipelineExecutor pipelineExecutor;

    @Override
    public void run(List<String> steps, Path path) throws PipelineRunnerException {

        for (String step : steps) {
            try {
                final int pipelineStepExecutionResult = this.pipelineExecutor.executePipelineStep(step, new File(path.toString()));

                if (pipelineStepExecutionResult != 0) {
                    throw new PipelineRunnerException(
                        "Pipeline step returned a non-zero status: " + pipelineStepExecutionResult,
                        step,
                        path.toString()
                    );
                }

            } catch (PipelineExecutorException e) {
                throw new PipelineRunnerException(
                    "Pipeline runner exception occurred running the pipeline step",
                    step,
                    path.toString(),
                    e
                );
            }
        }

    }
}
