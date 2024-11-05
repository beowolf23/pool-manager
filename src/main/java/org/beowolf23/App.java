package org.beowolf23;

import net.schmizz.sshj.SSHClient;
import org.beowolf23.pool.GenericResponse;
import org.beowolf23.pool.ManagedConnectionObjectFactory;
import org.beowolf23.pool.ManagedConnectionPool;
import org.beowolf23.ssh.SSHConfiguration;
import org.beowolf23.ssh.SSHConnectionHandler;
import org.beowolf23.ssh.SSHJConnection;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        SSHConfiguration sshConfiguration = new SSHConfiguration("localhost", "22", "user", "bibi");
        SSHConnectionHandler sshConnectionHandler = new SSHConnectionHandler();
        ManagedConnectionObjectFactory<SSHConfiguration, SSHJConnection> factory = new ManagedConnectionObjectFactory<>(sshConnectionHandler);
        ManagedConnectionPool<SSHConfiguration, SSHJConnection> pool = new ManagedConnectionPool<>(factory);

        // Borrow a connection from the pool
        SSHJConnection sshConnection = null;
        try {
            sshConnection = pool.borrowObject(sshConfiguration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Execute a remote command using the borrowed connection
        GenericResponse<SSHJConnection> response = sshConnectionHandler.executeCommand(sshConnection, () -> "ls -l");

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

        // Return the connection to the pool
        System.out.println("Number of active connections: " + pool.getNumActive());
        System.out.println("Number of idle connections: " + pool.getNumIdle());
        pool.returnObject(sshConfiguration, sshConnection);

        System.out.println("=================================");
        System.out.println("Number of active connections: " + pool.getNumActive());
        System.out.println("Number of idle connections: " + pool.getNumIdle());

        // Shutdown the pool
        pool.close();
    }
}

