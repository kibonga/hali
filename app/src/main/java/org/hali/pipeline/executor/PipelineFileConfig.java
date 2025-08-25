package org.hali.pipeline.executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class PipelineFileConfig {

    @Bean
    @Qualifier("rawPipelineOutputPath")
    public Path rawPipelineOutputPath() throws IOException {
        return Files.createTempFile("pipeline-raw-", ".log");
    }

    @Bean
    @Qualifier("parsedPipelineOutputPath")
    public Path parsedPipelineOutputPath() throws IOException {
        return Files.createTempFile("pipeline-parsed-", ".log");
    }
}
