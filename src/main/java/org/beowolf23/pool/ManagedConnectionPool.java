package org.beowolf23.pool;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.beowolf23.ssh.SSHConfiguration;
import org.beowolf23.ssh.SSHConnectionHandler;
import org.beowolf23.ssh.SSHJConnection;

public class ManagedConnectionPool<T extends ConnectionConfiguration, V extends ManagedConnection> extends GenericKeyedObjectPool<T, V> {

    public ManagedConnectionPool(ManagedConnectionObjectFactory<T, V> factory) {
        this(factory, 20, 20, 40, 40);
    }

    public ManagedConnectionPool(ManagedConnectionObjectFactory<T, V> factory, int maxActive, int maxIdle, long idleTime, long maxWaitTime) {
        super(factory, new ManagedConnectionPoolConfig(maxActive, maxIdle, idleTime, maxWaitTime));
    }
}