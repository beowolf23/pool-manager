package org.beowolf23.exception;

public class ConnectionValidationException extends ConnectionPoolException {
    public ConnectionValidationException(String message) {
        super(message);
    }

    public ConnectionValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
