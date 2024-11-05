package org.beowolf23.ssh;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.beowolf23.pool.ConnectionHandler;
import org.beowolf23.pool.GenericResponse;
import org.beowolf23.pool.ManagedConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class SSHConnectionHandler implements ConnectionHandler<SSHConfiguration, SSHJConnection> {


    private SSHClient createClient(SSHConfiguration sshConfiguration) {
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
    public SSHJConnection connect(SSHConfiguration sshConfiguration) {
        try {
            // TODO maybe keep clients alive but close sessions, this way we can use less resources and invalidate only the sessions
            SSHClient client = createClient(sshConfiguration);
            Session session = client.startSession();
            return new SSHJConnection(client, session);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        return managedConnection.getClient().isConnected() && managedConnection.getSession().isOpen();
    }

    @Override
    public GenericResponse<SSHJConnection> executeCommand(SSHJConnection managedConnection, Supplier commandSupplier) {
        try {
            String command = (String) commandSupplier.get();
            Session.Command sessionCommand = managedConnection.getSession().exec(command);

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

            try (InputStream errorStream = sessionCommand.getErrorStream();
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream))) {
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    output.add(errorLine);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to read command error output: " + e.getMessage(), e);
            }

            GenericResponse<SSHJConnection> response = new GenericResponse<>();
            response.setStdout(output);
            return response;
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute SSH command: " + e.getMessage(), e);
        }
    }}
