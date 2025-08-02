package org.hali.git;

import org.hali.common.model.GithubEventContext;
import org.hali.exception.CloneRepositoryException;

import java.io.File;

public interface GitCommandExecutor {
    void clone(GithubEventContext githubEventContext, File workingDir) throws CloneRepositoryException;
}
