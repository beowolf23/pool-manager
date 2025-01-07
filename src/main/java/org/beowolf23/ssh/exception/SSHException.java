package org.beowolf23.ssh.exception;

public class SSHException extends RuntimeException {
    public SSHException(String message) {
        super(message);
    }

    public SSHException(String message, Throwable cause) {
        super(message, cause);
    }
}
