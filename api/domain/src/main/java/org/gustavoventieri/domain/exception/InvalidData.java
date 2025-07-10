package org.gustavoventieri.domain.exception;

public class InvalidData extends RuntimeException{
    public InvalidData() {
    }

    public InvalidData(String message) {
        super(message);
    }

    public InvalidData(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidData(Throwable cause) {
        super(cause);
    }
}