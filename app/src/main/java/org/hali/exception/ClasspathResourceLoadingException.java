package org.hali.exception;


public class ClasspathResourceLoadingException extends Exception {

    public ClasspathResourceLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClasspathResourceLoadingException(String message) {
        super(message);
    }
}
