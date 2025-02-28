package org.beowolf23.ssh.sshj;

import net.schmizz.sshj.connection.channel.direct.Session;
import org.beowolf23.command.AbstractCommandExecutor;
import org.beowolf23.pool.GenericResponse;
import org.beowolf23.pool.ManagedConnectionPool;
import org.beowolf23.ssh.SSHConfiguration;
import org.beowolf23.ssh.exception.SSHCommandExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SSHJCommandExecutor extends AbstractCommandExecutor<SSHConfiguration, SSHJConnection> {
    private static final Logger logger = LoggerFactory.getLogger(SSHJCommandExecutor.class);

    public SSHJCommandExecutor(ManagedConnectionPool<SSHConfiguration, SSHJConnection> pool) {
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
                if (sessionCommand.getExitStatus() != 0) {
                    logger.warn("Command executed with non-zero exit code: {}", sessionCommand.getExitStatus());
                } else {
                    logger.debug("Command executed successfully");
                }

                return response;
            } catch (Exception e) {
                logger.error("Failed to execute SSH command: {}", command, e);
                throw new SSHCommandExecutionException("Failed to execute SSH command", e);
            }
        };
    }

    private List<String> readCommandOutput(Session.Command cmd) {
        List<String> output = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(cmd.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
                logger.trace("Command output line: {}", line);
            }
        } catch (IOException e) {
            logger.error("Failed to read command output", e);
            throw new SSHCommandExecutionException("Failed to read command output", e);
        }
        return output;
    }
}
