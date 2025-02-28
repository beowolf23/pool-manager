package org.beowolf23.ssh.sshj;

import org.beowolf23.pool.Handler;
import org.beowolf23.ssh.SSHConfiguration;

public class SSHJConnectionHandler implements Handler<SSHConfiguration, SSHJConnection> {

    @Override
    public SSHJConnection connect(SSHConfiguration sshConfiguration) {
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
