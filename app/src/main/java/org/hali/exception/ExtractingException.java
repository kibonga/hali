package org.hali.exception;

public class ExtractingException extends Exception {

    public ExtractingException(String message) {
        super(message);
    }

    public ExtractingException(String message, Throwable cause) {
        super(message, cause);
    }
}
