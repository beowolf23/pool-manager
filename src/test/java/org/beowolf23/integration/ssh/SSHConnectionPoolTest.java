package org.beowolf23.integration.ssh;

import org.beowolf23.pool.GenericResponse;
import org.beowolf23.pool.ManagedConnectionPoolBuilder;
import org.beowolf23.ssh.SSHJConfiguration;
import org.beowolf23.ssh.SSHJConnectionHandler;
import org.beowolf23.ssh.SSHJConnection;
import org.beowolf23.integration.ConnectionPoolTestBase;
import org.beowolf23.pool.ManagedConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SSHConnectionPoolTest extends ConnectionPoolTestBase<SSHJConfiguration, SSHJConnection> {

    private static final int SSH_PORT = 22;
    private static GenericContainer<?> sshContainer;

    private final String CONTAINER_USERNAME = "root";
    private final String CONTAINER_PASSWORD = "root";

    @BeforeAll
    static void setUp() {
        sshContainer = new GenericContainer<>(DockerImageName.parse("rastasheep/ubuntu-sshd:latest"))
                .withExposedPorts(SSH_PORT);
        sshContainer.start();
    }

    @AfterAll
    public static void tearDown() {
        if (sshContainer != null) {
            sshContainer.stop();
        }
    }

    @Override
    protected ManagedConnectionPool<SSHJConfiguration, SSHJConnection> createPool() {
        return new ManagedConnectionPoolBuilder<SSHJConfiguration, SSHJConnection>()
                .withHandler(new SSHJConnectionHandler())
                .maxActive(10)
                .maxIdle(2)
                .maxWaitTime(20)
                .idleTime(0)
                .build();
    }

    @Override
    protected SSHJConfiguration createConfiguration() {
        Integer containerSshPort = sshContainer.getMappedPort(SSH_PORT);
        return new SSHJConfiguration(sshContainer.getHost(), containerSshPort.toString(),
                CONTAINER_USERNAME, CONTAINER_PASSWORD);
    }

    @Test
    void testExecuteCommandLs_success() throws Exception {
        ManagedConnectionPool<SSHJConfiguration, SSHJConnection> pool = createPool();
        SSHJConfiguration config = createConfiguration();

        GenericResponse<SSHJConnection> response = pool.executeCommand(config, () -> "ls -l");

        assertNotNull(response);
        assertEquals(List.of("total 0"), response.getStdout());
        assertEquals(0, response.getCode());
        assertNull(response.getException());
    }
}
