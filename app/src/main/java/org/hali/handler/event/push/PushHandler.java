package org.hali.handler.event.push;

import org.hali.common.fs.DirectoryService;
import org.hali.git.GitRepositoryCloner;
import org.hali.handler.event.GitEventHandler;
import org.hali.logging.StepStatusLogger;
import org.hali.metrics.CounterFactory;
import org.hali.metrics.LongRunningTaskFactory;
import org.hali.pipeline.PipelineMatchingContextBuilder;
import org.hali.pipeline.PipelineStepExtractor;
import org.hali.pipeline.responder.PipelineBuildStatusResponder;
import org.hali.pipeline.runner.PipelineRunner;
import org.hali.yaml.YamlParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PushHandler extends GitEventHandler {

    public PushHandler(
        DirectoryService directoryService,
        GitRepositoryCloner gitRepositoryCloner,
        YamlParser yamlParser,
        PipelineMatchingContextBuilder pipelineMatchingContextBuilder,
        PipelineStepExtractor pipelineStepExtractor,
        PipelineRunner pipelineRunner,
        PipelineBuildStatusResponder pipelineBuildStatusResponder,
        @Qualifier("pushHandlerLogger") StepStatusLogger stepStatusLogger,
        CounterFactory counterFactory,
        LongRunningTaskFactory longRunningTaskFactory
    ) {
        super(directoryService, gitRepositoryCloner, yamlParser, pipelineMatchingContextBuilder, pipelineStepExtractor, pipelineRunner, pipelineBuildStatusResponder, stepStatusLogger, counterFactory, longRunningTaskFactory);
    }
}
