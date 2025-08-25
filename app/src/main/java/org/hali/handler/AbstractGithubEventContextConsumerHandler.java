package org.hali.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hali.common.fs.DirectoryService;
import org.hali.common.fs.FileLoader;
import org.hali.common.model.GithubEventContext;
import org.hali.exception.CloneRepositoryException;
import org.hali.exception.PipelineRunnerException;
import org.hali.exception.YamlParsingException;
import org.hali.functional.ConsumerHandler;
import org.hali.git.GitRepositoryCloner;
import org.hali.pipeline.PipelineMatchingContext;
import org.hali.pipeline.PipelineMatchingContextBuilder;
import org.hali.pipeline.PipelineStepExtractor;
import org.hali.pipeline.responder.PipelineBuildStatusResponder;
import org.hali.pipeline.runner.PipelineRunner;
import org.hali.resource.ResourceLoader;
import org.hali.yaml.YamlParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unchecked")
public abstract class AbstractGithubEventContextConsumerHandler implements ConsumerHandler<GithubEventContext> {

    private static final String PIPELINE = "pipeline";
    private static final String YML_PIPELINE = "pipeline.yml";

    private static final Function<String, Path> yamlPathFunction = workingDir -> Path.of(workingDir, YML_PIPELINE);

    private final DirectoryService directoryService;
    private final GitRepositoryCloner gitRepositoryCloner;
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

//            try {
////                pipelineTempPath = Files.createTempDirectory("pipeline-");
//            } catch (IOException e) {
//                log.error("Failed to create temp dir for path: {}", pipelineTempPath, e);
//
//                return;
//            }
//            this.directoryService.create(pipelineTmpPath);

//            final Path projectPath = Paths.get(
//                pipelineTempPath.toString(),
//                githubEventContext.getProjectName()
//            );

            // Clone the repo
            Path pipelineTempDirPath = null;
            final String projectName = githubEventContext.getProjectName();
            try {
                pipelineTempDirPath = Files.createTempDirectory("pipeline-" + projectName + "-");
                log.info("Cloning the repository");
                this.gitRepositoryCloner.clone(githubEventContext.getRepoUrl(), pipelineTempDirPath.toFile());
            } catch (CloneRepositoryException e) {
                log.error("Error occurred while cloning the repository", e);

                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

//
//            // Get the yaml from the /tmp/cloned_repo
            final Path yamlpath = yamlPathFunction.apply(pipelineTempDirPath.toString());

            // load and execute pipeline.yml from cloned repo
            try {
//                final InputStream is = this.resourceLoader.getInputStream(yamlpath.toString());
                final InputStream is = Files.newInputStream(yamlpath);

                final Map<String, Object> yamlmap = this.yamlParser.parse(is);

                final PipelineMatchingContext pipelinematchingcontext = this.pipelineMatchingContextBuilder.build(githubEventContext);

                final Map<String, Object> pipelines = (Map<String, Object>) yamlmap.get("pipelines");

                final List<String> pipelineSteps = this.pipelineStepExtractor.extractSteps(pipelines, pipelinematchingcontext);

                final var pipelinePath = pipelineTempDirPath;
                final CompletableFuture<Boolean> runPipelineFuture = CompletableFuture.supplyAsync(() -> {
                    if (pipelineSteps.isEmpty()) {
                        log.info("No pipeline steps found.");
                        return false;
                    }

                    try {
                        return this.pipelineRunner.run(pipelineSteps, pipelinePath);
                    } catch (IOException e) {
                        log.error("Failed to run pipeline steps", e);
                        return false;
                    }
                });

                final var pipelineRunResult = runPipelineFuture.join();

                log.info("Pipeline run completed");

                if (pipelineRunResult) {
                    log.info("Successfully run the pipeline");
                } else {
                    log.info("Error running the pipeline");
                }


//                if (!pipelineSteps.isEmpty()) {
//                    this.pipelineRunner.run(pipelineSteps, pipelineTempDirPath);
//                } else {
//                    log.info("no pipeline steps found.");
//                }
            } catch (YamlParsingException |
                     PipelineRunnerException e) {
                log.error("error occurred parsing yaml file", e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            this.directoryService.remove(pipelineTempDirPath);

            // Respond to GitHub/Wiremock
//            try {
//                final BuildStatus buildstatus = new BuildStatus("success", "build-check", "pipeline successfully built");
//
//                this.pipelineBuildStatusResponder.send(buildstatus, githubEventContext.getBuildCheckUrl());
//            } catch (PipelineBuildStatusResponderException e) {
//                log.error("error occurred while building build status", e);
//            }

            log.info("Successfully cloned the repository.");
        };
    }

}
