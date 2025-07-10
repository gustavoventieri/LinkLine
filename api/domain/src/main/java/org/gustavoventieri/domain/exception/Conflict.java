package org.gustavoventieri.domain.exception;


public class Conflict extends RuntimeException {
    public Conflict() {
    }

    public Conflict(String message) {
        super(message);
    }

    public Conflict(String message, Throwable cause) {
        super(message, cause);
    }

    public Conflict(Throwable cause) {
        super(cause);
    }
}
