package org.beowolf23.pool;

public interface Handler<T extends Configuration, V extends Connection> {

    V connect(T t);

    void disconnect(V v);

    boolean isValid(V v);
}
