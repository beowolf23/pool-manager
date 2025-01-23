package org.beowolf23.exception;

public class PoolExhaustedException extends ConnectionPoolException {
    public PoolExhaustedException(String message) {
        super(message);
    }

    public PoolExhaustedException(String message, Throwable cause) {
        super(message, cause);
    }
}
