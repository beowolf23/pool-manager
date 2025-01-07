package org.beowolf23.ssh.exception;

public class SSHAuthenticationException extends SSHException {
    public SSHAuthenticationException(String message) {
        super(message);
    }

    public SSHAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
