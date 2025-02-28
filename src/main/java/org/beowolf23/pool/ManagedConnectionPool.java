package org.beowolf23.pool;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.beowolf23.exception.ConnectionPoolException;
import org.beowolf23.exception.PoolExhaustedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;

public class ManagedConnectionPool<T extends Configuration, V extends Connection> extends GenericKeyedObjectPool<T, V> {

    private static final Logger logger = LoggerFactory.getLogger(ManagedConnectionPool.class);

    private ManagedConnectionPoolConfig config;

    public ManagedConnectionPool(ManagedConnectionObjectFactory<T, V> factory, ManagedConnectionPoolConfig config) {
        super(factory, config);
    }

    @Override
    public V borrowObject(T key) {
        logger.debug("Attempting to borrow connection for key: {}", key.getHostname());
        try {
            V connection = super.borrowObject(key);
            logger.debug("Successfully borrowed connection for key: {}", key.getHostname());
            return connection;
        } catch (NoSuchElementException e) {
            logger.error("Pool exhausted while trying to borrow connection for key: {}", key.getHostname());
            throw new PoolExhaustedException("No available connections in pool", e);
        } catch (Exception e) {
            logger.error("Failed to borrow connection for key: {}", key.getHostname(), e);
            throw new ConnectionPoolException("Failed to borrow connection", e);
        }
    }

    @Override
    public void returnObject(T key, V obj) {
        try {
            logger.debug("Returning connection for key: {}", key.getHostname());
            super.returnObject(key, obj);
        } catch (Exception e) {
            logger.warn("Error returning connection to pool for key: {}", key.getHostname(), e);
            // We don't rethrow here as it's better to let the connection be garbage collected
            // than to throw an exception in the return path
        }
    }

    @Override
    public void invalidateObject(T key, V obj) {
        try {
            logger.info("Invalidating connection for key: {}", key.getHostname());
            super.invalidateObject(key, obj);
        } catch (Exception e) {
            logger.error("Error invalidating connection for key: {}", key.getHostname(), e);
            throw new ConnectionPoolException("Failed to invalidate connection", e);
        }
    }

    @Override
    public void close() {
        try {
            logger.info("Shutting down connection pool");
            super.close();
        } catch (Exception e) {
            logger.error("Error during pool shutdown", e);
            throw new ConnectionPoolException("Failed to shutdown connection pool", e);
        }
    }

    public void clearPool() {
        try {
            logger.info("Clearing all connections from pool");
            super.clear();
        } catch (Exception e) {
            logger.error("Error clearing pool", e);
            throw new ConnectionPoolException("Failed to clear connection pool", e);
        }
    }
}