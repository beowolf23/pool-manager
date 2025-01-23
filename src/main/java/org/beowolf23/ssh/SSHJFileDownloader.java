package org.beowolf23.ssh;

import net.schmizz.sshj.sftp.OpenMode;
import net.schmizz.sshj.sftp.SFTPClient;
import org.beowolf23.command.FileDownloader;
import org.beowolf23.pool.ManagedConnectionPool;
import org.beowolf23.ssh.exception.SSHFileTransferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.function.Function;

public class SSHJFileDownloader extends FileDownloader<SSHJConfiguration, SSHJConnection> {
    private static final Logger logger = LoggerFactory.getLogger(SSHJFileDownloader.class);

    public SSHJFileDownloader(ManagedConnectionPool<SSHJConfiguration, SSHJConnection> pool) {
        super(pool);
    }

    @Override
    public Function<SSHJConnection, OutputStream> fileToBeDownloaded(String remoteFilePath) {
        return sshjConnection -> {
            logger.debug("Starting file download from: {}", remoteFilePath);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try (SFTPClient sftpClient = sshjConnection.getClient().newSFTPClient()) {
                logger.debug("SFTP client created successfully");

                try (InputStream is = sftpClient.open(remoteFilePath,
                        EnumSet.of(OpenMode.READ)).new RemoteFileInputStream()) {

                    byte[] buffer = new byte[8192];
                    long totalBytesTransferred = 0;
                    int bytesRead;

                    while ((bytesRead = is.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        totalBytesTransferred += bytesRead;
                        logger.trace("Downloaded {} bytes", totalBytesTransferred);
                    }

                    logger.info("Successfully downloaded {} bytes from {}",
                            totalBytesTransferred, remoteFilePath);
                    return outputStream;
                }
            } catch (IOException e) {
                logger.error("Failed to download file from: {}", remoteFilePath, e);
                throw new SSHFileTransferException("Failed to download file", e);
            }
        };
    }
}