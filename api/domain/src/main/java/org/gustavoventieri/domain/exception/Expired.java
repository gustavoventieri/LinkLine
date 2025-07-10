package org.gustavoventieri.domain.exception;

public class Expired extends RuntimeException {
    public Expired() {
    }

    public Expired(String message) {
        super(message);
    }

    public Expired(String message, Throwable cause) {
        super(message, cause);
    }

    public Expired(Throwable cause) {
        super(cause);
    }
}
