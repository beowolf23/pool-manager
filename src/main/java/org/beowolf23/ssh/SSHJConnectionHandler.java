package org.beowolf23.ssh;

import org.beowolf23.pool.ConnectionHandler;

public class SSHJConnectionHandler implements ConnectionHandler<SSHJConfiguration, SSHJConnection> {

    @Override
    public SSHJConnection connect(SSHJConfiguration sshConfiguration) {
        return SSHJConnection.createConnected(sshConfiguration);
    }

    @Override
    public void disconnect(SSHJConnection managedConnection) {
        managedConnection.disconnect();
    }

    @Override
    public boolean isValid(SSHJConnection managedConnection) {
        return managedConnection.isValid();
    }

}
