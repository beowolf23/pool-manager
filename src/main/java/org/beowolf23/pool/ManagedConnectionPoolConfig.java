package org.beowolf23.pool;

import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import java.time.Duration;

public class ManagedConnectionPoolConfig extends GenericKeyedObjectPoolConfig {

    public ManagedConnectionPoolConfig(int maxActive, int maxIdle, long idleTime, long maxWaitTime) {
        this.setMaxTotalPerKey(maxActive);
        this.setMaxIdlePerKey(maxIdle);
        this.setMaxWait(Duration.ofSeconds(maxWaitTime));
        this.setMinEvictableIdleDuration(Duration.ofSeconds(idleTime));
        this.setBlockWhenExhausted(true);
        this.setTestOnBorrow(true);
        this.setTestOnReturn(true);
        this.setTestWhileIdle(true);
        this.setJmxEnabled(false);
    }
}
