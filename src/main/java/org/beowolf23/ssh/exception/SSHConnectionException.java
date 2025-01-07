package org.beowolf23.ssh.exception;

public class SSHConnectionException extends SSHException {
    public SSHConnectionException(String message) {
        super(message);
    }

    public SSHConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
