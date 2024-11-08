package org.beowolf23.integration.ssh;

import org.beowolf23.ssh.SSHJConfiguration;
import org.beowolf23.ssh.SSHJConnectionHandler;
import org.beowolf23.ssh.SSHJConnection;
import org.beowolf23.integration.ConnectionPoolTestBase;
import org.beowolf23.pool.ManagedConnectionObjectFactory;
import org.beowolf23.pool.ManagedConnectionPool;

public class SSHConnectionPoolTest extends ConnectionPoolTestBase<SSHJConfiguration, SSHJConnection> {
    @Override
    protected ManagedConnectionPool<SSHJConfiguration, SSHJConnection> createPool() {
        SSHJConnectionHandler sshConnectionHandler = new SSHJConnectionHandler();
        return new ManagedConnectionPool<>(new ManagedConnectionObjectFactory<>(sshConnectionHandler));
    }

    @Override
    protected SSHJConfiguration createConfiguration() {
        return new SSHJConfiguration("localhost", "22", "user", "bibi");
    }
}
