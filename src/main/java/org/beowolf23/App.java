package org.beowolf23;

import net.schmizz.sshj.SSHClient;
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
        SSHConnectionHandler handler = new SSHConnectionHandler();
        ManagedConnectionObjectFactory<SSHConfiguration, SSHJConnection> factory = new ManagedConnectionObjectFactory(handler);
        ManagedConnectionPool<SSHConfiguration, SSHJConnection> pool = new ManagedConnectionPool<>(factory);

        SSHConfiguration configuration = new SSHConfiguration("localhost", "22", "user", "bibi");

        SSHJConnection connection;
        try {
            connection = pool.borrowObject(configuration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
