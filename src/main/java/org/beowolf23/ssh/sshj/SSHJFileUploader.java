package org.beowolf23.ssh.sshj;

import net.schmizz.sshj.sftp.OpenMode;
import net.schmizz.sshj.sftp.SFTPClient;
import org.beowolf23.command.FileUploader;
import org.beowolf23.pool.ManagedConnectionPool;
import org.beowolf23.ssh.SSHConfiguration;
import org.beowolf23.ssh.exception.SSHFileTransferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.function.Consumer;

public class SSHJFileUploader extends FileUploader<SSHConfiguration, SSHJConnection> {
    private static final Logger logger = LoggerFactory.getLogger(SSHJFileUploader.class);

    public SSHJFileUploader(ManagedConnectionPool<SSHConfiguration, SSHJConnection> pool) {
        super(pool);
    }

    @Override
    public Consumer<SSHJConnection> fileToBeUploaded(InputStream inputStream, String remoteFilePath) {
        return sshjConnection -> {
            logger.debug("Starting file upload to: {}", remoteFilePath);

            try (SFTPClient sftpClient = sshjConnection.getClient().newSFTPClient()) {
                logger.debug("SFTP client created successfully");

                try (OutputStream os = sftpClient.open(remoteFilePath,
                        EnumSet.of(OpenMode.CREAT, OpenMode.WRITE)).new RemoteFileOutputStream()) {

                    byte[] buffer = new byte[8192];
                    long totalBytesTransferred = 0;
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                        totalBytesTransferred += bytesRead;
                        logger.trace("Uploaded {} bytes", totalBytesTransferred);
                    }

                    logger.info("Successfully uploaded {} bytes to {}", totalBytesTransferred, remoteFilePath);
                }
            } catch (IOException e) {
                logger.error("Failed to upload file to: {}", remoteFilePath, e);
                throw new SSHFileTransferException("Failed to upload file", e);
            }
        };
    }
}