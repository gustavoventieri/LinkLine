package org.gustavoventieri.domain.exception;


public class NotFound extends RuntimeException {
    public NotFound() {
    }

    public NotFound(String message) {
        super(message);
    }

    public NotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFound(Throwable cause) {
        super(cause);
    }
}