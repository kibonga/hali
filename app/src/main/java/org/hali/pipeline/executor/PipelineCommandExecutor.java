package org.hali.pipeline.executor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hali.common.command.AbstractCommandExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Slf4j
@Service
public class PipelineCommandExecutor extends AbstractCommandExecutor {
    private final Path rawPipelineOutputPath;
    private final Path parsedPipelineOutputPath;

    public PipelineCommandExecutor(
        @Qualifier("rawPipelineOutputPath") Path rawPipelineOutputPath,
        @Qualifier("parsedPipelineOutputPath") Path parsedPipelineOutputPath
    ) {
        this.rawPipelineOutputPath = rawPipelineOutputPath;
        this.parsedPipelineOutputPath = parsedPipelineOutputPath;
    }

    @Override
    protected void handleOutput(String line) throws IOException {
        log.info(line);
        Files.writeString(this.rawPipelineOutputPath, line + System.lineSeparator(), StandardOpenOption.APPEND);
    }

    @Override
    public void cleanUp() throws IOException {
        Files.deleteIfExists(this.rawPipelineOutputPath);
        Files.deleteIfExists(this.parsedPipelineOutputPath);
    }
}
