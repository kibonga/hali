package org.hali.git;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hali.common.command.CommandExecutor;
import org.hali.common.model.GithubEventContext;
import org.hali.exception.CloneRepositoryException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractGitCommandExecutor implements GitCommandExecutor {

    private final CommandExecutor commandExecutor;


    @Override
    public void clone(GithubEventContext githubEventContext, File workingDir) throws CloneRepositoryException {
        log.info("Cloning repository from branch [{}]: [{}]", githubEventContext.getBranch(), githubEventContext.getRepoUrl());

        final var cloneCommandFunction = getCloneCommandFunction();

        final var cloneCommand = cloneCommandFunction.apply(githubEventContext.getBranch(), githubEventContext.getRepoUrl());

        try {
            log.info("Running the command: {} for file: {}", cloneCommand,
                workingDir);

            final int commandResult = this.commandExecutor.runCommand(cloneCommand, workingDir);

            if (commandResult != 0) {
                log.error("Failed to clone repository [{}] from branch [{}]",
                    githubEventContext.getRepoUrl(), githubEventContext.getBranch());

                throw new CloneRepositoryException(
                    "Failed to clone repository",
                    githubEventContext.getRepoUrl(),
                    githubEventContext.getBranch());

            }
        } catch (IOException e) {
            throw new CloneRepositoryException(
                "I/O error occurred while cloning the repository",
                githubEventContext.getRepoUrl(), githubEventContext.getBranch(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CloneRepositoryException(
                "Interrupt error occurred while cloning the repository",
                githubEventContext.getRepoUrl(), githubEventContext.getBranch(), e);
        }

        log.info("Successfully cloned the repository: {} for branch: {}",
            githubEventContext.getRepoUrl(), githubEventContext.getBranch());
    }

    protected abstract BiFunction<String, String, List<String>> getCloneCommandFunction();
}
