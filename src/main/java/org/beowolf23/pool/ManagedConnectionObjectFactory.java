package org.beowolf23.pool;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class ManagedConnectionObjectFactory<T extends ConnectionConfiguration, V extends ManagedConnection> extends BaseKeyedPooledObjectFactory<T, V> {

    private final ConnectionHandler<T, V> connectionHandler;

    public ManagedConnectionObjectFactory(ConnectionHandler<T, V> connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    @Override
    public V create(T key) {
        return connectionHandler.connect(key);
    }

    @Override
    public PooledObject<V> wrap(V value) {
        return new DefaultPooledObject<>(value);
    }

    @Override
    public void destroyObject(T key, PooledObject<V> p) throws Exception {
        connectionHandler.disconnect(p.getObject());
    }

    @Override
    public boolean validateObject(T key, PooledObject<V> p) {
        return connectionHandler.isValid(p.getObject());
    }
}
