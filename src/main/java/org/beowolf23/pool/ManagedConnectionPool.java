package org.beowolf23.pool;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

import java.util.function.Supplier;

public class ManagedConnectionPool<T extends ConnectionConfiguration, V extends ManagedConnection> extends GenericKeyedObjectPool<T, V> {

    public ManagedConnectionPool(ManagedConnectionObjectFactory<T, V> factory) {
        this(factory, 20, 20, 40, 40);
    }

    public ManagedConnectionPool(ManagedConnectionObjectFactory<T, V> factory, int maxActive, int maxIdle, long idleTime, long maxWaitTime) {
        super(factory, new ManagedConnectionPoolConfig(maxActive, maxIdle, idleTime, maxWaitTime));
    }

    public GenericResponse<V> executeCommand(T key, Supplier supplier) throws Exception {
        ConnectionHandler<T, V> handler = ((ManagedConnectionObjectFactory) this.getFactory()).getConnectionHandler();
        V connection = this.borrowObject(key);
        GenericResponse<V> response = handler.executeCommand(connection, supplier);
        this.returnObject(key, connection);
        return response;
    }
}