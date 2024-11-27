package org.beowolf23.command;

import org.beowolf23.pool.ConnectionConfiguration;
import org.beowolf23.pool.GenericResponse;
import org.beowolf23.pool.ManagedConnection;

public interface CommandExecutor<T extends ConnectionConfiguration, V extends ManagedConnection> {

    GenericResponse<V> executeCommand(T t, String command) throws Exception;
}
