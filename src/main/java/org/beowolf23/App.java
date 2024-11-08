package org.beowolf23;

import org.beowolf23.pool.*;
import org.beowolf23.ssh.SSHJConfiguration;
import org.beowolf23.ssh.SSHJConnectionHandler;
import org.beowolf23.ssh.SSHJConnection;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        SSHJConfiguration sshConfiguration = new SSHJConfiguration("localhost", "22", "user", "bibi");

        ManagedConnectionPool<SSHJConfiguration, SSHJConnection> pool = new ManagedConnectionPoolBuilder<SSHJConfiguration, SSHJConnection>()
                .withHandler(new SSHJConnectionHandler())
                .maxActive(10)
                .maxIdle(0)
                .maxWaitTime(20)
                .idleTime(0)
                .build();

        GenericResponse<SSHJConnection> response;
        try {
            response = pool.executeCommand(sshConfiguration, () -> "ls -l");
            Thread.sleep(1000);
            System.out.println(pool.getNumIdle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Execute a remote command using the borrowed connection

        // Check the result
        if (response.getCode() == 0) {
            System.out.println("Command execution successful:");
            for (String line : response.getStdout()) {
                System.out.println(line);
            }
        } else {
            System.err.println("Command execution failed with exit code: " + response.getCode());
            if (response.getException() != null) {
                response.getException().printStackTrace();
            }
        }

        // Shutdown the pool
        pool.close();
    }
}

