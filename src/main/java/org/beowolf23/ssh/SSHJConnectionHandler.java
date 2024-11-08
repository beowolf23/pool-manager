package org.beowolf23.ssh;

import net.schmizz.sshj.connection.channel.direct.Session;
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

    @Override
    public SSHJConnection connect(SSHJConfiguration sshConfiguration) {
        return SSHJConnection.createConnected(sshConfiguration);
    }

    @Override
    public void disconnect(SSHJConnection managedConnection) {
        managedConnection.disconnect();
    }

    @Override
    public boolean isValid(SSHJConnection managedConnection) {
        return managedConnection.isValid();
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
