package org.beowolf23.pool;

public class ManagedConnectionPoolBuilder<T extends ConnectionConfiguration, V extends ManagedConnection> {

    private ConnectionHandler<T, V> connectionHandler;
    private ManagedConnectionObjectFactory<T, V> factory;
    private int maxActive = 20;  // default
    private int maxIdle = 5;     // default
    private long idleTime = 30;  // default in seconds
    private long maxWaitTime = 10; // default in seconds

    // Set the ConnectionHandler
    public ManagedConnectionPoolBuilder<T, V> withHandler(ConnectionHandler<T, V> handler) {
        this.connectionHandler = handler;
        return this;
    }

    // Set the Object Factory explicitly
    public ManagedConnectionPoolBuilder<T, V> withFactory(ManagedConnectionObjectFactory<T, V> factory) {
        this.factory = factory;
        return this;
    }

    // Set pool configurations
    public ManagedConnectionPoolBuilder<T, V> maxActive(int maxActive) {
        this.maxActive = maxActive;
        return this;
    }

    public ManagedConnectionPoolBuilder<T, V> maxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
        return this;
    }

    public ManagedConnectionPoolBuilder<T, V> idleTime(long idleTime) {
        this.idleTime = idleTime;
        return this;
    }

    public ManagedConnectionPoolBuilder<T, V> maxWaitTime(long maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
        return this;
    }

    // Build method to create the ManagedConnectionPool
    public ManagedConnectionPool<T, V> build() {
        // Validation to ensure handler and factory are set or can be derived
        if (connectionHandler == null) {
            throw new IllegalStateException("ConnectionHandler is required to build the pool");
        }

        if (factory == null) {
            // If factory is not set explicitly, create one using the provided handler
            factory = new ManagedConnectionObjectFactory<>(connectionHandler);
        }

        return new ManagedConnectionPool<>(factory, maxActive, maxIdle, idleTime, maxWaitTime);
    }
}
