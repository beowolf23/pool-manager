package org.beowolf23.ssh.jsch;

import com.jcraft.jsch.ChannelExec;
import org.beowolf23.command.AbstractCommandExecutor;
import org.beowolf23.pool.GenericResponse;
import org.beowolf23.pool.ManagedConnectionPool;
import org.beowolf23.ssh.SSHConfiguration;
import org.beowolf23.ssh.exception.SSHCommandExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class JschCommandExecutor extends AbstractCommandExecutor<SSHConfiguration, JschConnection> {
    private static final Logger logger = LoggerFactory.getLogger(JschCommandExecutor.class);

    public JschCommandExecutor(ManagedConnectionPool<SSHConfiguration, JschConnection> pool) {
        super(pool);
    }

    @Override
    public Function<JschConnection, GenericResponse<JschConnection>> commandToBeExecuted(String command) {
        return connection -> {
            GenericResponse<JschConnection> response = new GenericResponse<>();
            try {
                ChannelExec channel = (ChannelExec) connection.getSession().openChannel("exec");
                channel.setCommand(command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));

                channel.connect();
                List<String> output = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.add(line);
                    logger.trace("Command output: {}", line);
                }

                response.setStdout(output);
                channel.disconnect();
                return response;
            } catch (Exception e) {
                logger.error("Command execution failed: {}", command, e);
                throw new SSHCommandExecutionException("Command failed: " + command, e);
            }
        };
    }
}