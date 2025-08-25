package org.hali.pipeline.executor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hali.common.command.CommandExecutor;
import org.hali.exception.PipelineExecutorException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultPipelineExecutor implements PipelineExecutor {

    private final CommandExecutor commandExecutor;

    @Override
    public int executePipelineStep(String step, File file) throws PipelineExecutorException {
        try {
            return this.commandExecutor.runCommand(
                prepareBashCommandWithExitOnError(step),
                file
            );
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            throw new PipelineExecutorException(
                "Pipeline runner failed for step: [{}] on path: [{}]",
                step,
                file.toPath().toString(),
                e
            );
        }
    }

    @Override
    public void cleanUp() throws IOException {
        this.commandExecutor.cleanUp();
    }

    private static List<String> prepareBashCommandWithExitOnError(String step) {
        return new ArrayList<>(List.of("bash", "-c", "(set -e; " + step + ")"));
    }
}
