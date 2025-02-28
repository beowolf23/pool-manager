package org.beowolf23.ssh.jsch;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.beowolf23.pool.Connection;
import org.beowolf23.ssh.SSHConfiguration;
import org.beowolf23.ssh.exception.SSHConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JschConnection extends Connection {

    private final Session session;

    private static Logger logger = LoggerFactory.getLogger(JschConnection.class);

    public JschConnection(Session session) {
        this.session = session;
    }

    public void connect() throws JSchException {
        session.connect();
    }

    public void disconnect() {
        session.disconnect();
    }

    public boolean isValid() {
        return session.isConnected();
    }
    public Session getSession() {
        return session;
    }

    public static JschConnection createConnected(SSHConfiguration config) {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(config.getUsername(), config.getHostname(), Integer.parseInt(config.getPort()));
            session.setPassword(config.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            return new JschConnection(session);
        } catch (JSchException e) {
            throw new SSHConnectionException("JSCH connection failed", e);
        }
    }
}
