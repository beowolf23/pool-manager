package org.beowolf23.pool;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ConnectionHandler<T extends ConnectionConfiguration, V extends ManagedConnection> {

     V connect(T t);
     void disconnect(V v);
     boolean isValid(V v);
     GenericResponse<V> executeCommand(V v, Supplier supplier);

}
