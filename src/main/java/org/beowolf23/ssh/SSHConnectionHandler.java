package org.beowolf23.ssh;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import org.beowolf23.pool.ConnectionHandler;
import org.beowolf23.pool.GenericResponse;
import org.beowolf23.pool.ManagedConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class SSHConnectionHandler implements ConnectionHandler<SSHConfiguration, SSHJConnection, SSHClient> {

    private SSHClient client;

    public SSHConnectionHandler(SSHClient client) {
        this.client = client;
    }

    private SSHClient createClient(SSHConfiguration sshConfiguration) {
        try {
            client.connect(sshConfiguration.getHostname());
            client.authPassword(sshConfiguration.getUsername(), sshConfiguration.getPassword());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this.client;
    }

    @Override
    public SSHJConnection connect(SSHConfiguration sshConfiguration) {
        try {
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
        return managedConnection.getClient().isConnected();
    }

    @Override
    public GenericResponse<SSHJConnection> executeCommand(SSHJConnection managedConnection, Supplier supplier) {
        try {
            Session.Command command = managedConnection.getSession().exec((String) supplier.get());

            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(command.getInputStream()));
            // TODO read from stderr in case the command was not successful
            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(command.getErrorStream()));
            String line;
            GenericResponse response = new GenericResponse();
            List<String> lines = new ArrayList<>();
            try {
                while((line = stdoutReader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            response.setStdout(lines);
            response.setException(null);
            response.setCode(0);
            return response;
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        } catch (TransportException e) {
            throw new RuntimeException(e);
        }
    }

}
