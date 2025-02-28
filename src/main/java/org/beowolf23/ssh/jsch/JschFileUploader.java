package org.beowolf23.ssh.jsch;

import com.jcraft.jsch.ChannelSftp;
import org.beowolf23.command.FileUploader;
import org.beowolf23.pool.ManagedConnectionPool;
import org.beowolf23.ssh.SSHConfiguration;
import org.beowolf23.ssh.exception.SSHFileTransferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.function.Consumer;

public class JschFileUploader extends FileUploader<SSHConfiguration, JschConnection> {
    private static final Logger logger = LoggerFactory.getLogger(JschFileUploader.class);

    public JschFileUploader(ManagedConnectionPool<SSHConfiguration, JschConnection> pool) {
        super(pool);
    }

    @Override
    public Consumer<JschConnection> fileToBeUploaded(InputStream inputStream, String remoteFilePath) {
        return connection -> {
            try {
                ChannelSftp channel = (ChannelSftp) connection.getSession().openChannel("sftp");
                channel.connect();
                channel.put(inputStream, remoteFilePath);
                channel.disconnect();
                logger.info("File uploaded to {}", remoteFilePath);
            } catch (Exception e) {
                logger.error("Upload failed: {}", remoteFilePath, e);
                throw new SSHFileTransferException("Upload failed: " + remoteFilePath, e);
            }
        };
    }
}