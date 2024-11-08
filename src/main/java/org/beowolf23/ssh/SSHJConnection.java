package org.beowolf23.ssh;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.beowolf23.pool.ManagedConnection;

import java.io.IOException;

public class SSHJConnection extends ManagedConnection {

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
                client.disconnect();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error disconnecting SSH client", e);
        }
    }

    public boolean isValid() {
        return client != null && client.isConnected();
    }

    public static SSHJConnection createConnected(SSHJConfiguration sshjConfiguration) {
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());

        try {
            client.connect(sshjConfiguration.getHostname(), Integer.parseInt(sshjConfiguration.getPort()));
            client.authPassword(sshjConfiguration.getUsername(), sshjConfiguration.getPassword());
            return new SSHJConnection(client);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create SSH connection", e);
        }
    }
}

