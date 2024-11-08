package org.beowolf23.smb;

import org.beowolf23.pool.ConnectionConfiguration;

public class SMBJConfiguration extends ConnectionConfiguration {

    public SMBJConfiguration(String hostname, String port, String username, String password) {
        super(hostname, port, username, password);
    }
}
