package org.beowolf23.pool;

public abstract class ManagedConnection {

    public abstract void connect();
    public abstract void disconnect();
    public abstract boolean isValid();
}
