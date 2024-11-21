package org.beowolf23.ssh;

import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import org.beowolf23.command.AbstractCommandExecutor;
import org.beowolf23.command.FileDownloader;
import org.beowolf23.command.FileUploader;
import org.beowolf23.pool.GenericResponse;
import org.beowolf23.pool.ManagedConnectionPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SSHJCommandExecutor extends AbstractCommandExecutor<SSHJConfiguration, SSHJConnection> implements FileDownloader, FileUploader {

    public SSHJCommandExecutor(ManagedConnectionPool<SSHJConfiguration, SSHJConnection> pool) {
        super(pool);
    }

    public GenericResponse<SSHJConnection> executeCommand(SSHJConfiguration sshjConfiguration, String command) throws Exception {
        return super.executeCommand(sshjConfiguration, managedConnection -> {
            Session session;
            Session.Command sessionCommand;
            GenericResponse<SSHJConnection> response = new GenericResponse<>();

            try {
                session = managedConnection.getClient().startSession();
                sessionCommand = session.exec(command);

                List<String> output = readCommandOutput(sessionCommand);
                response.setStdout(output);
                session.close();
            } catch (ConnectionException | TransportException e) {
                throw new RuntimeException(e);
            }

            return response;
        });
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
