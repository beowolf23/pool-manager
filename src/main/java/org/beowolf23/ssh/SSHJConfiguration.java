package org.beowolf23.ssh;

import org.beowolf23.pool.ConnectionConfiguration;

public class SSHJConfiguration extends ConnectionConfiguration {

    public SSHJConfiguration(String hostname, String port, String username, String password) {
        super(hostname, port, username, password);
    }
}
