package org.beowolf23.command;

import org.beowolf23.pool.ConnectionConfiguration;
import org.beowolf23.pool.GenericResponse;
import org.beowolf23.pool.ManagedConnection;
import org.beowolf23.pool.ManagedConnectionPool;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractCommandExecutor<T extends ConnectionConfiguration, V extends ManagedConnection> implements CommandExecutor<T, V> {

    private ManagedConnectionPool<T, V> pool;

    public AbstractCommandExecutor(ManagedConnectionPool<T, V> pool) {
        this.pool = pool;
    }

    @Override
    public GenericResponse<V> executeCommand(T t, Function<V, GenericResponse<V>> function) throws Exception {
        V v = pool.borrowObject(t);
        GenericResponse<V> response = function.apply(v);
        pool.returnObject(t, v);
        return response;
    }
}
