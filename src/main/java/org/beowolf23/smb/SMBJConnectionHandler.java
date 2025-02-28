package org.beowolf23.smb;

import org.beowolf23.pool.Handler;

public class SMBJConnectionHandler implements Handler<SMBJConfiguration, SMBJConnection> {

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

}
