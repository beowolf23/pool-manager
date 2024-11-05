package org.beowolf23.pool;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.beowolf23.ssh.SSHConnectionHandler;

public class ManagedConnectionPool<T extends ConnectionConfiguration, V extends ManagedConnection> extends GenericKeyedObjectPool<T, V> {


    public ManagedConnectionPool() {
        this(20, 20, 40, 40);
    }

    public ManagedConnectionPool(int maxActive, int maxIdle, long idleTime, long maxWaitTime) {
        super(new ManagedConnectionObjectFactory<T, V>(new SSHConnectionHandler()), new ManagedConnectionPoolConfig(maxActive, maxIdle, idleTime, maxWaitTime));
    }

    public ManagedConnectionPool(ManagedConnectionObjectFactory<T, V> factory) {
        super(factory);
    }
}
