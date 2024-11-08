package org.beowolf23.ssh;

import org.beowolf23.ConnectionPoolTestBase;
import org.beowolf23.pool.ManagedConnectionObjectFactory;
import org.beowolf23.pool.ManagedConnectionPool;

public class SSHConnectionPoolTest extends ConnectionPoolTestBase<SSHConfiguration, SSHJConnection> {
    @Override
    protected ManagedConnectionPool<SSHConfiguration, SSHJConnection> createPool() {
        SSHConnectionHandler sshConnectionHandler = new SSHConnectionHandler();
        return new ManagedConnectionPool<>(new ManagedConnectionObjectFactory<>(sshConnectionHandler));
    }

    @Override
    protected SSHConfiguration createConfiguration() {
        return new SSHConfiguration("localhost", "22", "user", "bibi");
    }
}
