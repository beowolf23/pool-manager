package org.beowolf23.ssh;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.beowolf23.pool.ConnectionHandler;
import org.beowolf23.pool.GenericResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SSHJConnectionHandler implements ConnectionHandler<SSHJConfiguration, SSHJConnection> {

    private SSHClient createClient(SSHJConfiguration sshConfiguration) {
        SSHClient client;
        try {
            client = new SSHClient();
            client.addHostKeyVerifier(new PromiscuousVerifier());
            client.connect(sshConfiguration.getHostname());
            client.authPassword(sshConfiguration.getUsername(), sshConfiguration.getPassword());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return client;
    }

    @Override
    public SSHJConnection connect(SSHJConfiguration sshConfiguration) {
        SSHClient client = createClient(sshConfiguration);
        return new SSHJConnection(client);
    }

    @Override
    public void disconnect(SSHJConnection managedConnection) {
        try {
            managedConnection.getClient().disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isValid(SSHJConnection managedConnection) {
        return managedConnection.getClient().isConnected();
    }

    @Override
    public GenericResponse<SSHJConnection> executeCommand(SSHJConnection managedConnection, Supplier commandSupplier) {
        try {

            Session session = managedConnection.getClient().startSession();

            String command = (String) commandSupplier.get();
            Session.Command sessionCommand = session.exec(command);

            List<String> output = readCommandOutput(sessionCommand);

            GenericResponse<SSHJConnection> response = new GenericResponse<>();
            response.setStdout(output);
            session.close();

            return response;
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute SSH command: " + e.getMessage(), e);
        }
    }

    private List<String> readCommandOutput(Session.Command sessionCommand) {
        List<String> output = new ArrayList<>();
        try (InputStream inputStream = sessionCommand.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read command output: " + e.getMessage(), e);
        }
        return output;
    }
}
