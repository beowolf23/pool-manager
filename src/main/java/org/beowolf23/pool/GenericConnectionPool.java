package org.beowolf23.pool;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

public class GenericConnectionPool<T extends ConnectionConfiguration, V extends ManagedConnection> extends GenericKeyedObjectPool<T, V> {


    public GenericConnectionPool() {
        this(20, 20, 40, 40);
    }

    public GenericConnectionPool(int maxActive, int maxIdle, long idleTime,  long maxWaitTime) {
        super(new GenericConnectionFactory<>(), new GenericConnectionPoolConfig(maxActive, maxIdle, idleTime, maxWaitTime));
    }
}
