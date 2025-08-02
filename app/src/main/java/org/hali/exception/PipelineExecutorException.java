package org.hali.exception;

import lombok.Getter;

@Getter
public class PipelineExecutorException extends Exception {

    private final String step;
    private final String path;

    public PipelineExecutorException(String message, String step, String path,
                                     Throwable cause) {
        super(message, cause);
        this.step = step;
        this.path = path;
    }

    public PipelineExecutorException(String message, String step, String path) {
        super(message);
        this.step = step;
        this.path = path;
    }
}
