package org.beowolf23.ssh;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import org.beowolf23.pool.ManagedConnection;

public class SSHJConnection extends ManagedConnection {

    private SSHClient client;
    private Session session;

    public SSHJConnection(SSHClient client, Session session) {
        this.client = client;
        this.session = session;
    }

    public SSHClient getClient() {
        return client;
    }

    public Session getSession() {
        return session;
    }
}

