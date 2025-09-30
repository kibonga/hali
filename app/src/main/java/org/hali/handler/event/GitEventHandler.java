package org.hali.handler.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.StringMapMessage;
import org.hali.common.fs.DirectoryService;
import org.hali.common.model.BuildStatus;
import org.hali.exception.CloneRepositoryException;
import org.hali.exception.PipelineBuildStatusResponderException;
import org.hali.exception.PipelineRunnerException;
import org.hali.exception.YamlParsingException;
import org.hali.functional.ConsumerHandler;
import org.hali.git.GitRepositoryCloner;
import org.hali.handler.event.logging.GitEventHandlerStep;
import org.hali.handler.webhook.domain.WebhookContext;
import org.hali.logging.StepStatusLogger;
import org.hali.metrics.CounterFactory;
import org.hali.metrics.LongRunningTaskFactory;
import org.hali.pipeline.PipelineMatchingContext;
import org.hali.pipeline.PipelineMatchingContextBuilder;
import org.hali.pipeline.PipelineStepExtractor;
import org.hali.pipeline.responder.PipelineBuildStatusResponder;
import org.hali.pipeline.runner.PipelineRunner;
import org.hali.yaml.YamlParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.hali.handler.event.logging.GitEventHandlerStep.CLONE_REPOSITORY_ERROR;
import static org.hali.handler.event.logging.GitEventHandlerStep.CLONE_REPOSITORY_SUCCESS;
import static org.hali.handler.event.logging.GitEventHandlerStep.CREATE_TEMP_DIR_SUCCESS;
import static org.hali.handler.event.logging.GitEventHandlerStep.PARSE_PIPELINE_ERROR;
import static org.hali.handler.event.logging.GitEventHandlerStep.PARSE_PIPELINE_SUCCESS;
import static org.hali.handler.event.logging.GitEventHandlerStep.PIPELINE_STEPS_NOT_FOUND_ERROR;
import static org.hali.handler.event.logging.GitEventHandlerStep.REMOVE_TEMP_DIR_SUCCESS;
import static org.hali.handler.event.logging.GitEventHandlerStep.RUN_PIPELINE_ERROR;
import static org.hali.handler.event.logging.GitEventHandlerStep.RUN_PIPELINE_SUCCESS;
import static org.hali.handler.event.logging.GitEventHandlerStep.UNKNOWN_ERROR;
import static org.hali.handler.event.logging.GitEventHandlerStep.WEBHOOK_RESPONSE_ERROR;
import static org.hali.handler.event.logging.GitEventHandlerStep.WEBHOOK_RESPONSE_SUCCESS;

@RequiredArgsConstructor
@Log4j2
@SuppressWarnings("unchecked")
public abstract class GitEventHandler implements ConsumerHandler<WebhookContext> {
    private static final String PIPELINE_YAML = "pipeline.yml";

    private final DirectoryService directoryService;
    private final GitRepositoryCloner gitRepositoryCloner;
    private final YamlParser yamlParser;
    private final PipelineMatchingContextBuilder pipelineMatchingContextBuilder;
    private final PipelineStepExtractor pipelineStepExtractor;
    private final PipelineRunner pipelineRunner;
    private final PipelineBuildStatusResponder pipelineBuildStatusResponder;
    private final StepStatusLogger stepStatusLogger;
    private final CounterFactory counterFactory;
    private final LongRunningTaskFactory longRunningTaskFactory;

    @Override
    public Consumer<WebhookContext> consumer() {
        return webhookContext -> {
            final String projectName = webhookContext.getProjectName();
            Path pipelineDirTempPath = null;

            try {
                pipelineDirTempPath = Files.createTempDirectory("pipeline-" + projectName);
                this.stepStatusLogger.logInfo(CREATE_TEMP_DIR_SUCCESS, "successfully created temp dir");

                // Step 1 - Clone the repository
                this.gitRepositoryCloner.clone(webhookContext.getRepoUrl(), pipelineDirTempPath.toFile());
                this.stepStatusLogger.logInfo(CLONE_REPOSITORY_SUCCESS, "successfully cloned repository");

                // Step 2 - Extract pipeline steps
                final Map<String, Object> pipelineYamlMap = parsePipelineYaml(pipelineDirTempPath);
                this.stepStatusLogger.logInfo(PARSE_PIPELINE_SUCCESS, "successfully parsed pipeline yaml");

                final PipelineMatchingContext pipelinematchingcontext = this.pipelineMatchingContextBuilder.build(webhookContext);

                final List<String> pipelineSteps = this.pipelineStepExtractor.extractSteps((Map<String, Object>) pipelineYamlMap.get("pipelines"), pipelinematchingcontext);
                if (pipelineSteps.isEmpty()) {
                    this.stepStatusLogger.logError(PIPELINE_STEPS_NOT_FOUND_ERROR, "not found", "pipeline steps not found", null);
                    return;
                }

                // Step 3 - Run the pipeline
                final boolean pipelineResult = this.pipelineRunner.run(pipelineSteps, pipelineDirTempPath);
                if (pipelineResult) {
                    this.stepStatusLogger.logInfo(RUN_PIPELINE_SUCCESS, "successfully run pipeline steps");
                } else {
                    this.stepStatusLogger.logError(RUN_PIPELINE_ERROR, "pipeline result: " + pipelineResult, "pipeline run failed", null);
                }

            } catch (CloneRepositoryException e) {
                this.stepStatusLogger.logError(CLONE_REPOSITORY_ERROR, e.getMessage(), "failed to clone repository", e);
                return;
            } catch (YamlParsingException e) {
                this.stepStatusLogger.logError(PARSE_PIPELINE_ERROR, e.getMessage(), "failed to parse yaml", e);
                return;
            } catch (PipelineRunnerException e) {
                this.stepStatusLogger.logError(RUN_PIPELINE_ERROR, e.getMessage(), "pipeline run failed", e);
                return;
            } catch (Exception e) {
                this.stepStatusLogger.logError(UNKNOWN_ERROR, e.getMessage(), "unknown error", e);
                return;
            } finally {
                this.directoryService.remove(pipelineDirTempPath);
                this.stepStatusLogger.logInfo(REMOVE_TEMP_DIR_SUCCESS, "successfully removed temp dir");
            }

            // Step 4 - Respond to webhook
            try {
                final BuildStatus buildstatus = new BuildStatus("success", "build-check", "pipeline successfully built");
                this.pipelineBuildStatusResponder.send(buildstatus, webhookContext.getBuildCheckUrl());
                this.stepStatusLogger.logInfo(WEBHOOK_RESPONSE_SUCCESS, "successfully sent webhook response");
            } catch (PipelineBuildStatusResponderException e) {
                this.stepStatusLogger.logError(WEBHOOK_RESPONSE_ERROR, e.getMessage(), "failed to send webhook response", e);
            } catch (Exception e) {
                log.error("failed to send webhook response", e);
            }
        };
    }

    private Map<String, Object> parsePipelineYaml(Path repoDir) throws IOException, YamlParsingException {
        final Path pipelineYamlPath = resolvePipelineYamlPath(repoDir);
        final InputStream is = Files.newInputStream(pipelineYamlPath);
        return this.yamlParser.parse(is);
    }

    private static Path resolvePipelineYamlPath(Path repoDir) {
        return Path.of(repoDir.toString(), PIPELINE_YAML);
    }

    private StringMapMessage getEventHandlerLog(GitEventHandlerStep step, String projectName) {
//        return GitEventHandlerLoggingFactory.getStringMapMessage(step, handlerType(), projectName);
        return null;
    }
}
