package org.hali.git;

import org.hali.exception.CloneRepositoryException;

import java.io.File;

public interface GitRepositoryCloner {
    void clone(String url, File workingDir) throws CloneRepositoryException;
}
