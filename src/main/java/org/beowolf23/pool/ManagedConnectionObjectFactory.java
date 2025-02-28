package org.beowolf23.pool;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.beowolf23.exception.ConnectionCreationException;
import org.beowolf23.exception.ConnectionPoolException;
import org.beowolf23.exception.ConnectionValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagedConnectionObjectFactory<T extends Configuration, V extends Connection> extends BaseKeyedPooledObjectFactory<T, V> {

    private static final Logger logger = LoggerFactory.getLogger(ManagedConnectionObjectFactory.class);

    private final Handler<T, V> connectionHandler;

    public ManagedConnectionObjectFactory(Handler<T, V> connectionHandler) {
        this.connectionHandler = connectionHandler;
        logger.debug("Initializing connection factory with handler: {}", connectionHandler.getClass().getSimpleName());
    }

    @Override
    public V create(T key) {
        try {
            logger.debug("Creating new connection for key: {}", key.getHostname());
            V connection = connectionHandler.connect(key);
            logger.info("Successfully created connection for host: {}", key.getHostname());
            return connection;
        } catch (Exception e) {
            logger.error("Failed to create connection for host: {}", key.getHostname(), e);
            throw new ConnectionCreationException("Failed to create connection for host: " + key.getHostname(), e);
        }
    }

    @Override
    public PooledObject<V> wrap(V value) {
        logger.trace("Wrapping connection object in PooledObject");
        return new DefaultPooledObject<>(value);
    }

    @Override
    public void destroyObject(T key, PooledObject<V> p) {
        try {
            logger.debug("Destroying connection for host: {}", key.getHostname());
            connectionHandler.disconnect(p.getObject());
            logger.info("Successfully destroyed connection for host: {}", key.getHostname());
        } catch (Exception e) {
            logger.error("Error destroying connection for host: {}", key.getHostname(), e);
            throw new ConnectionPoolException("Failed to destroy connection", e);
        }
    }

    @Override
    public boolean validateObject(T key, PooledObject<V> p) {
        try {
            logger.debug("Validating connection for host: {}", key.getHostname());
            boolean isValid = connectionHandler.isValid(p.getObject());
            if (!isValid) {
                logger.warn("Connection validation failed for host: {}", key.getHostname());
            }
            return isValid;
        } catch (Exception e) {
            logger.error("Error validating connection for host: {}", key.getHostname(), e);
            throw new ConnectionValidationException("Failed to validate connection", e);
        }
    }

}