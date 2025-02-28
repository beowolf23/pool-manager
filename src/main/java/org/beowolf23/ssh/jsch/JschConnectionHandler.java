package org.beowolf23.ssh.jsch;

import org.beowolf23.pool.Handler;
import org.beowolf23.ssh.SSHConfiguration;

public class JschConnectionHandler implements Handler<SSHConfiguration, JschConnection> {

    @Override
    public JschConnection connect(SSHConfiguration config) {
        return JschConnection.createConnected(config);
    }

    @Override
    public void disconnect(JschConnection conn) {
        conn.disconnect();
    }

    @Override
    public boolean isValid(JschConnection conn) {
        return conn.isValid();
    }
}
