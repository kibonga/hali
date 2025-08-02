package org.hali.exception;

import lombok.Getter;

@Getter
public class PipelineRunnerException extends RuntimeException {

    private final String step;
    private final String path;

    public PipelineRunnerException(String message, String step, String path, Throwable cause) {
        super(message, cause);
        this.step = step;
        this.path = path;
    }

    public PipelineRunnerException(String message, String step, String path) {
        super(message);
        this.step = step;
        this.path = path;
    }

}

