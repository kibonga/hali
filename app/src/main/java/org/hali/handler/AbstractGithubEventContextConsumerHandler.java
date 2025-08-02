package org.hali.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hali.common.fs.DirectoryService;
import org.hali.common.fs.FileLoader;
import org.hali.common.model.BuildStatus;
import org.hali.common.model.GithubEventContext;
import org.hali.exception.ClasspathResourceLoadingException;
import org.hali.exception.CloneRepositoryException;
import org.hali.exception.PipelineBuildStatusResponderException;
import org.hali.exception.PipelineRunnerException;
import org.hali.exception.YamlParsingException;
import org.hali.functional.ConsumerHandler;
import org.hali.git.GitCommandExecutor;
import org.hali.pipeline.PipelineMatchingContext;
import org.hali.pipeline.PipelineMatchingContextBuilder;
import org.hali.pipeline.PipelineStepExtractor;
import org.hali.pipeline.responder.PipelineBuildStatusResponder;
import org.hali.pipeline.runner.PipelineRunner;
import org.hali.resource.ResourceLoader;
import org.hali.yaml.YamlParser;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unchecked")
public abstract class AbstractGithubEventContextConsumerHandler implements ConsumerHandler<GithubEventContext> {

    private static final String PIPELINE = "pipeline";
    private static final String YML_PIPELINE = "pipeline.yml";

    private static final BiFunction<String, String, Path> yamlPathFunction = (workingDir, project) -> Path.of(workingDir, project, YML_PIPELINE);

    private final DirectoryService directoryService;
    private final GitCommandExecutor gitCommandExecutor;
    private final FileLoader fileLoader;
    private final ResourceLoader resourceLoader;
    private final YamlParser yamlParser;
    private final PipelineMatchingContextBuilder pipelineMatchingContextBuilder;
    private final PipelineStepExtractor pipelineStepExtractor;
    private final PipelineRunner pipelineRunner;
    private final PipelineBuildStatusResponder pipelineBuildStatusResponder;

    @Override
    public Consumer<GithubEventContext> consumer() {
        return githubEventContext -> {

            // Create tmp dir where to clone the repo
            log.info("Creating temp dir.");
            final String tmpDir = System.getProperty("java.io.tmpdir");
            final Path pipelineTmpPath = Path.of(tmpDir, PIPELINE);

            this.directoryService.create(pipelineTmpPath);

            final Path projectPath = Paths.get(
                pipelineTmpPath.toString(),
                githubEventContext.getProjectName()
            );

            // Clone the repo
            try {
                log.info("Cloning the repository");
                this.gitCommandExecutor.clone(githubEventContext, new File(pipelineTmpPath.toString()));
            } catch (CloneRepositoryException e) {
                log.error("Error occurred while cloning the repository", e);

                this.directoryService.remove(projectPath);

                return;
            }

            // Get the yaml from the /tmp/cloned_repo
            final Path yamlPath = yamlPathFunction.apply(pipelineTmpPath.toString(), githubEventContext.getProjectName());

            // Load and execute pipeline.yml from cloned repo
            try {
                final InputStream is = this.resourceLoader.getInputStream(yamlPath.toString());

                final Map<String, Object> yamlMap = this.yamlParser.parse(is);

                final PipelineMatchingContext pipelineMatchingContext = this.pipelineMatchingContextBuilder.build(githubEventContext);

                final List<String> pipelineSteps = this.pipelineStepExtractor.extractSteps(yamlMap, pipelineMatchingContext);

                if (!pipelineSteps.isEmpty()) {
                    this.pipelineRunner.run(pipelineSteps, projectPath);
                } else {
                    log.info("No pipeline steps found.");
                }
            } catch (ClasspathResourceLoadingException | YamlParsingException |
                     PipelineRunnerException e) {
                log.error("Error occurred parsing yaml file", e);
            } finally {
                this.directoryService.remove(projectPath);
            }

            // Respond to GitHub/Wiremock
            try {
                final BuildStatus buildStatus = new BuildStatus("success", "build-check", "Pipeline successfully built");

                this.pipelineBuildStatusResponder.send(buildStatus, githubEventContext.getBuildCheckUrl());
            } catch (PipelineBuildStatusResponderException e) {
                log.error("Error occurred while building build status", e);
            }
        };
    }

}
