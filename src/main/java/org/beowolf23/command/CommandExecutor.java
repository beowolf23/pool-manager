package org.beowolf23.command;

import org.beowolf23.pool.Configuration;
import org.beowolf23.pool.GenericResponse;
import org.beowolf23.pool.Connection;

public interface CommandExecutor<T extends Configuration, V extends Connection> {

    GenericResponse<V> executeCommand(T t, String command) throws Exception;
}
