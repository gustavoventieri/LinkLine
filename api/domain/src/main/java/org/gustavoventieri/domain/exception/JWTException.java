package org.gustavoventieri.domain.exception;

public class JWTException extends RuntimeException {
    public JWTException() {
    }

    public JWTException(String message) {
        super(message);
    }

    public JWTException(String message, Throwable cause) {
        super(message, cause);
    }

    public JWTException(Throwable cause) {
        super(cause);
    }
}
