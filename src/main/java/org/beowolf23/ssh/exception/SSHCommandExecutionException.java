package org.beowolf23.ssh.exception;

public class SSHCommandExecutionException extends SSHException {
    public SSHCommandExecutionException(String message) {
        super(message);
    }

    public SSHCommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
