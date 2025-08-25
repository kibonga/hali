package org.hali.handler.pullrequest;

import lombok.extern.slf4j.Slf4j;
import org.hali.common.fs.DirectoryService;
import org.hali.common.fs.FileLoader;
import org.hali.git.GitRepositoryCloner;
import org.hali.handler.AbstractGithubEventContextConsumerHandler;
import org.hali.pipeline.PipelineMatchingContextBuilder;
import org.hali.pipeline.PipelineStepExtractor;
import org.hali.pipeline.responder.PipelineBuildStatusResponder;
import org.hali.pipeline.runner.PipelineRunner;
import org.hali.resource.ResourceLoader;
import org.hali.yaml.YamlParser;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PullRequestHandler extends AbstractGithubEventContextConsumerHandler {

    public PullRequestHandler(
        DirectoryService directoryService,
        GitRepositoryCloner gitRepositoryCloner,
        FileLoader fileLoader,
        ResourceLoader resourceLoader,
        YamlParser yamlParser,
        PipelineMatchingContextBuilder pipelineMatchingContextBuilder,
        PipelineStepExtractor pipelineStepExtractor,
        PipelineRunner pipelineRunner,
        PipelineBuildStatusResponder pipelineBuildStatusResponder
    ) {
        super(directoryService, gitRepositoryCloner, fileLoader, resourceLoader, yamlParser, pipelineMatchingContextBuilder, pipelineStepExtractor, pipelineRunner, pipelineBuildStatusResponder);
    }
}
