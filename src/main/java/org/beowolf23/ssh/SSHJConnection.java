package org.beowolf23.ssh;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.beowolf23.pool.ManagedConnection;
import org.beowolf23.ssh.exception.SSHAuthenticationException;
import org.beowolf23.ssh.exception.SSHConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class SSHJConnection extends ManagedConnection {
    private static final Logger logger = LoggerFactory.getLogger(SSHJConnection.class);
    private final SSHClient client;

    private SSHJConnection(SSHClient client) {
        this.client = client;
    }

    public SSHClient getClient() {
        return client;
    }

    public void disconnect() {
        try {
            if (client.isConnected()) {
                logger.debug("Disconnecting SSH client");
                client.disconnect();
                logger.info("SSH client successfully disconnected");
            }
        } catch (IOException e) {
            logger.error("Error disconnecting SSH client", e);
            throw new SSHConnectionException("Error disconnecting SSH client", e);
        }
    }

    public boolean isValid() {
        boolean isValid = client != null && client.isConnected();
        logger.debug("Checking SSH connection validity: {}", isValid);
        return isValid;
    }

    public static SSHJConnection createConnected(SSHJConfiguration config) {
        logger.debug("Creating new SSH connection to {}:{}", config.getHostname(), config.getPort());
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());

        try {
            client.connect(config.getHostname(), Integer.parseInt(config.getPort()));
            logger.info("Successfully established SSH connection to {}:{}",
                    config.getHostname(), config.getPort());

            authenticateClient(client, config);

            return new SSHJConnection(client);
        } catch (IOException e) {
            logger.error("Failed to establish SSH connection to {}:{}",
                    config.getHostname(), config.getPort(), e);
            throw new SSHConnectionException("Failed to create SSH connection", e);
        }
    }

    private static void authenticateClient(SSHClient client, SSHJConfiguration config) {
        try {
            client.authPassword(config.getUsername(), config.getPassword());
            logger.info("Successfully authenticated user {} on {}:{}",
                    config.getUsername(), config.getHostname(), config.getPort());
        } catch (IOException e) {
            logger.error("Authentication failed for user {} on {}:{}",
                    config.getUsername(), config.getHostname(), config.getPort());
            throw new SSHAuthenticationException("Failed to authenticate SSH connection", e);
        }
    }
}

