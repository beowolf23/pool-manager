package org.beowolf23.ssh;

import org.beowolf23.pool.ConnectionConfiguration;

public class SSHConfiguration extends ConnectionConfiguration {

    public SSHConfiguration(String hostname, String port, String username, String password) {
        super(hostname, port, username, password);
    }
}
