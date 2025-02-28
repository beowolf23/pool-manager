package org.beowolf23.integration.ssh;

import org.beowolf23.pool.GenericResponse;
import org.beowolf23.pool.ManagedConnectionPool;
import org.beowolf23.pool.ManagedConnectionPoolBuilder;
import org.beowolf23.ssh.SSHConfiguration;
import org.beowolf23.ssh.sshj.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class SSHConnectionPoolTest {

    private static final int SSH_PORT = 22;
    private static GenericContainer<?> sshContainer;

    private static final String CONTAINER_USERNAME = "root";
    private static final String CONTAINER_PASSWORD = "root";

    private static final String CONTAINER_IMAGE = "rastasheep/ubuntu-sshd:latest";

    private static ManagedConnectionPool<SSHConfiguration, SSHJConnection> pool;
    private static SSHConfiguration sshjConfiguration;
    private static SSHJCommandExecutor sshjCommandExecutor;
    private static SSHJFileUploader sshjFileUploader;
    private static SSHJFileDownloader sshjFileDownloader;

    @BeforeAll
    static void setUp() {
        sshContainer = new GenericContainer<>(DockerImageName.parse(CONTAINER_IMAGE))
                .withExposedPorts(SSH_PORT);
        sshContainer.start();

        pool = createPool();
        sshjConfiguration = createConfiguration();

        sshjCommandExecutor = new SSHJCommandExecutor(pool);
        sshjFileUploader = new SSHJFileUploader(pool);
        sshjFileDownloader = new SSHJFileDownloader(pool);
    }

    @AfterAll
    public static void tearDown() {
        if (sshContainer != null) {
            sshContainer.stop();
        }
    }

    protected static ManagedConnectionPool<SSHConfiguration, SSHJConnection> createPool() {
        return new ManagedConnectionPoolBuilder<SSHConfiguration, SSHJConnection>()
                .withHandler(new SSHJConnectionHandler())
                .maxActive(10)
                .maxIdle(2)
                .maxWaitTime(20)
                .idleTime(0)
                .build();
    }

    protected static SSHConfiguration createConfiguration() {
        Integer containerSshPort = sshContainer.getMappedPort(SSH_PORT);
        return new SSHConfiguration(sshContainer.getHost(), containerSshPort.toString(),
                CONTAINER_USERNAME, CONTAINER_PASSWORD);
    }

    @Test
    void when_executingCommandLs_then_executesSuccessfully() throws Exception {
        assertThatCode(() -> sshjCommandExecutor.executeCommand(sshjConfiguration, "ls"))
                .doesNotThrowAnyException();

        GenericResponse<SSHJConnection> response = sshjCommandExecutor.executeCommand(sshjConfiguration, "pwd");

        assertThat(response.getStdout())
                .isNotNull()
                .isNotEmpty()
                .contains("/root")
                .doesNotContain("unexpectedPath");
    }

    @Test
    void when_uploadingAFile_then_uploadSuccessfully() {

        assertThatCode(() -> sshjFileUploader.uploadFile(sshjConfiguration, new ByteArrayInputStream("test".getBytes()), "/root/test.txt"))
                .doesNotThrowAnyException();
    }

    @Test
    void when_downloadingAFileAfterUploading_then_downloadSuccessfully() throws Exception {

        assertThatCode(() -> sshjFileUploader.uploadFile(sshjConfiguration, new ByteArrayInputStream("test".getBytes()), "/root/test.txt"))
                .doesNotThrowAnyException();

        OutputStream os = sshjFileDownloader.downloadFile(sshjConfiguration, "/root/test.txt");
        assertThat(os.toString()).hasToString("test");
    }

}
