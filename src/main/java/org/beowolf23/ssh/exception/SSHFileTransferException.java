package org.beowolf23.ssh.exception;

public class SSHFileTransferException extends SSHException {
    public SSHFileTransferException(String message) {
        super(message);
    }

    public SSHFileTransferException(String message, Throwable cause) {
        super(message, cause);
    }
}
