package org.beowolf23.exception;

public class ConnectionCreationException extends ConnectionPoolException {
    public ConnectionCreationException(String message) {
        super(message);
    }

    public ConnectionCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}