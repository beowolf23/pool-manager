package org.beowolf23.ssh;

import net.schmizz.sshj.SSHClient;
import org.beowolf23.pool.ManagedConnection;

public class SSHJConnection extends ManagedConnection {

    private final SSHClient client;

    public SSHJConnection(SSHClient client) {
        this.client = client;
    }

    public SSHClient getClient() {
        return client;
    }

}

