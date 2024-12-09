package org.beowolf23.ssh;

import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import org.beowolf23.command.AbstractCommandExecutor;
import org.beowolf23.pool.GenericResponse;
import org.beowolf23.pool.ManagedConnectionPool;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SSHJCommandExecutor extends AbstractCommandExecutor<SSHJConfiguration, SSHJConnection> {

    public SSHJCommandExecutor(ManagedConnectionPool<SSHJConfiguration, SSHJConnection> pool) {
        super(pool);
    }

    @Override
    public Function<SSHJConnection, GenericResponse<SSHJConnection>> commandToBeExecuted(String command) {
        return sshjConnection -> {
            Session session;
            Session.Command sessionCommand;
            GenericResponse<SSHJConnection> response = new GenericResponse<>();

            try {
                session = sshjConnection.getClient().startSession();
                sessionCommand = session.exec(command);

                List<String> output = readCommandOutput(sessionCommand);
                response.setStdout(output);
                session.close();
            } catch (ConnectionException | TransportException e) {
                throw new RuntimeException(e);
            }

            return response;
        };
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

//    @Override
//    public OutputStream downloadFile(SSHJConfiguration sshjConfiguration, String remoteFilePath) throws Exception {
//
//        ManagedConnectionPool<SSHJConfiguration, SSHJConnection> pool = getPool();
//        SSHJConnection connection = pool.borrowObject(sshjConfiguration);
//
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        try (SSHClient client = connection.getClient();
//             SFTPEngine engine = client.newSFTPClient().getSFTPEngine();
//             RemoteFile file = engine.open(remoteFilePath, EnumSet.of(OpenMode.READ));
//             InputStream is = file.new RemoteFileInputStream()) {
//            is.transferTo(os);
//        }
//        pool.returnObject(sshjConfiguration, connection);
//        return os;
//    }
//
//    public void uploadFile(SSHJConfiguration sshjConfiguration, InputStream is, String remoteFilePath) throws Exception {
//        SSHJConnection connection = getPool().borrowObject(sshjConfiguration);
//
//        try (SSHClient client = connection.getClient();
//             SFTPEngine engine = client.newSFkTPClient().getSFTPEngine();
//             RemoteFile file = engine.open(remoteFilePath, EnumSet.of(OpenMode.CREAT, OpenMode.WRITE));
//             OutputStream os = file.new RemoteFileOutputStream(0, 10)) {
//
//            is.transferTo(os);
//        }
//        getPool().returnObject(sshjConfiguration, connection);
//    }
}
