package org.beowolf23.pool;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class GenericConnectionFactory<T extends ConnectionConfiguration, V extends ManagedConnection> extends BaseKeyedPooledObjectFactory<T, V> {
    @Override
    public V create(T key) {
        
    }

    @Override
    public PooledObject<V> wrap(V value) {
        return new DefaultPooledObject<>(value);
    }

    @Override
    public void destroyObject(T key, PooledObject<V> p) throws Exception {
        p.getObject().disconnect();
        super.destroyObject(key, p);
    }

    @Override
    public boolean validateObject(T key, PooledObject<V> p) {
        return p.getObject().isValid();
    }
}
