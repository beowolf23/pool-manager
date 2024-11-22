package org.beowolf23.integration.ssh;

import org.beowolf23.pool.GenericResponse;
import org.beowolf23.pool.ManagedConnectionPoolBuilder;
import org.beowolf23.ssh.SSHJCommandExecutor;
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

import static org.assertj.core.api.Assertions.*;

public class SSHConnectionPoolTest extends ConnectionPoolTestBase<SSHJConfiguration, SSHJConnection> {

    private static final int SSH_PORT = 22;
    private static GenericContainer<?> sshContainer;

    private final String CONTAINER_USERNAME = "root";
    private final String CONTAINER_PASSWORD = "root";

    private static final String CONTAINER_IMAGE = "rastasheep/ubuntu-sshd:latest";

    private ManagedConnectionPool<SSHJConfiguration, SSHJConnection> pool = createPool();
    private SSHJConfiguration config = createConfiguration();
    private SSHJCommandExecutor sshjCommandExecutor = new SSHJCommandExecutor(pool);

    @BeforeAll
    static void setUp() {
        sshContainer = new GenericContainer<>(DockerImageName.parse(CONTAINER_IMAGE))
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
    void when_executingCommandLs_then_executesSuccessfully() throws Exception {
        assertThatCode(() -> sshjCommandExecutor.executeCommand(config, "ls"))
                .doesNotThrowAnyException();

        GenericResponse<SSHJConnection> response = sshjCommandExecutor.executeCommand(config, "pwd");

        assertThat(response.getStdout())
                .isNotNull()
                .isNotEmpty()
                .contains("/root")
                .doesNotContain("unexpectedPath");
    }

}
