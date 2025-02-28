package org.beowolf23.integration.ssh;

import org.beowolf23.pool.GenericResponse;
import org.beowolf23.pool.ManagedConnectionPool;
import org.beowolf23.pool.ManagedConnectionPoolBuilder;
import org.beowolf23.ssh.SSHConfiguration;
import org.beowolf23.ssh.jsch.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class JschConnectionPoolTest {

    private static final Integer SSH_PORT = 22;
    private static final String CONTAINER_USERNAME = "root";
    private static final String CONTAINER_PASSWORD = "root";
    private static final String CONTAINER_IMAGE = "rastasheep/ubuntu-sshd:latest";

    @Container
    private static final GenericContainer<?> sshContainer = new GenericContainer<>(DockerImageName.parse(CONTAINER_IMAGE))
            .withExposedPorts(SSH_PORT);

    private static ManagedConnectionPool<SSHConfiguration, JschConnection> pool;
    private static SSHConfiguration sshConfiguration;
    private static JschCommandExecutor executor;
    private static JschFileUploader uploader;
    private static JschFileDownloader downloader;

    @BeforeAll
    static void setUp() {
        sshContainer.start();

        pool = createPool();
        sshConfiguration = createConfiguration();

        executor = new JschCommandExecutor(pool);
        uploader = new JschFileUploader(pool);
        downloader = new JschFileDownloader(pool);
    }

    private static ManagedConnectionPool<SSHConfiguration, JschConnection> createPool() {
        return new ManagedConnectionPoolBuilder<SSHConfiguration, JschConnection>()
                .withHandler(new JschConnectionHandler())
                .maxActive(10)
                .maxIdle(2)
                .maxWaitTime(20)
                .idleTime(0)
                .build();
    }

    private static SSHConfiguration createConfiguration() {
        Integer containerSshPort = sshContainer.getMappedPort(SSH_PORT);
        return new SSHConfiguration(sshContainer.getHost(), containerSshPort.toString(), CONTAINER_USERNAME, CONTAINER_PASSWORD);
    }

    @Test
    void when_ExecuteCommand_thenSuccess() throws Exception {
        GenericResponse<JschConnection> response = executor.executeCommand(sshConfiguration, "ls -la");
        assertNotNull(response);
        assertFalse(response.getStdout().isEmpty());
        System.out.println("Command Output: " + response.getStdout());
    }

    @Test
    void when_UploadFile_thenSuccess() throws Exception {
        String remoteFilePath = "/tmp/testfile.txt";
        String fileContent = "Hello, this is a test file!";

        try (InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8))) {
            uploader.uploadFile(sshConfiguration, inputStream, remoteFilePath);
        }

        // Verify the file was uploaded by executing a command to check its existence
        GenericResponse<JschConnection> response = executor.executeCommand(sshConfiguration, "cat " + remoteFilePath);
        assertNotNull(response);
        assertEquals(fileContent, String.join("\n", response.getStdout()));
    }

    @Test
    public void whenDownloadFile_thenSuccess() throws Exception {
        String remoteFilePath = "/tmp/testfile.txt";
        String fileContent = "Hello, this is a test file!";

        // Upload a file first
        try (InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8))) {
            uploader.uploadFile(sshConfiguration, inputStream, remoteFilePath);
        }

        ByteArrayOutputStream downloadedStream = (ByteArrayOutputStream) downloader.downloadFile(sshConfiguration, remoteFilePath);

        String downloadedContent = downloadedStream.toString();
        assertEquals(fileContent, downloadedContent);
    }

    @Test
    void whenUploadLargeFile_thenMeasureUploadSpeed() throws Exception {
        // Generate a 20 MB file
        long fileSizeInBytes = 20 * 1024 * 1024; // 20 MB
        File largeFile = generateFile(fileSizeInBytes, "upload-test-", ".dat");
        String remoteFilePath = "/tmp/largefile.dat";

        // Measure upload time
        long startTime = System.nanoTime();

        try (InputStream inputStream = new FileInputStream(largeFile)) {
            uploader.uploadFile(sshConfiguration, inputStream, remoteFilePath);
        }

        long endTime = System.nanoTime();
        long durationInNanos = endTime - startTime;
        double durationInSeconds = durationInNanos / 1_000_000_000.0;

        // Calculate upload speed
        double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024.0);
        double uploadSpeedMBps = fileSizeInMB / durationInSeconds;

        // Log the results
        System.out.printf("Uploaded a %.2f MB file in %.2f seconds (%.2f MB/s)%n",
                fileSizeInMB, durationInSeconds, uploadSpeedMBps);

        // Verify the file was uploaded
        GenericResponse<JschConnection> response = executor.executeCommand(sshConfiguration, "ls -lh " + remoteFilePath);
        assertNotNull(response);
        assertFalse(response.getStdout().isEmpty());
        System.out.println("Uploaded file details: " + response.getStdout());
    }

    private static File generateFile(long sizeInBytes, String prefix, String suffix) throws IOException, IOException {
        File file = File.createTempFile(prefix, suffix);
        file.deleteOnExit(); // Ensure the file is deleted when the JVM exits

        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024 * 8]; // 8 KB buffer
            Random random = new Random();
            long bytesWritten = 0;

            while (bytesWritten < sizeInBytes) {
                random.nextBytes(buffer); // Fill buffer with random data
                int bytesToWrite = (int) Math.min(buffer.length, sizeInBytes - bytesWritten);
                fos.write(buffer, 0, bytesToWrite);
                bytesWritten += bytesToWrite;
            }
        }

        return file;
    }

    @AfterAll
    static void tearDown() {
        if (sshContainer != null) {
            sshContainer.stop();
        }
    }
}