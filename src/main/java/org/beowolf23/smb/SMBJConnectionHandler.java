package org.beowolf23.smb;

import org.beowolf23.pool.ConnectionHandler;
import org.beowolf23.pool.GenericResponse;

import java.util.function.Supplier;

public class SMBJConnectionHandler implements ConnectionHandler<SMBJConfiguration, SMBJConnection> {

    @Override
    public SMBJConnection connect(SMBJConfiguration smbjConfiguration) {
        return null;
    }

    @Override
    public void disconnect(SMBJConnection smbjConnection) {

    }

    @Override
    public boolean isValid(SMBJConnection smbjConnection) {
        return false;
    }

    @Override
    public GenericResponse<SMBJConnection> executeCommand(SMBJConnection smbjConnection, Supplier supplier) {
        return null;
    }
}
