package org.beowolf23.ssh;

import org.beowolf23.pool.Configuration;

public class SSHConfiguration extends Configuration {

    public SSHConfiguration(String hostname, String port, String username, String password) {
        super(hostname, port, username, password);
    }
}
