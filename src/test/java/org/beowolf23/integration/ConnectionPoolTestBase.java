package org.beowolf23.integration;

import org.beowolf23.pool.ConnectionConfiguration;
import org.beowolf23.pool.ManagedConnection;
import org.beowolf23.pool.ManagedConnectionPool;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public abstract class ConnectionPoolTestBase<T extends ConnectionConfiguration, V extends ManagedConnection> {

    protected abstract ManagedConnectionPool<T, V> createPool();
    protected abstract T createConfiguration();

    @Test
    public void when_borrowingAndReturningConnection_then_poolStateIsUpdatedCorrectly() throws Exception {
        ManagedConnectionPool<T, V> pool = createPool();
        T config = createConfiguration();

        V connection = pool.borrowObject(config);

        assertNotNull(connection);
        assertEquals(1, pool.getNumActive(), "Number of active connections should be 1 after borrowing");

        pool.returnObject(config, connection);

        assertEquals(1, pool.getNumIdle(), "Number of idle connections should be 1 after returning");
        assertEquals(0, pool.getNumActive(), "Number of active connections should be 0 after returning");
    }
}
