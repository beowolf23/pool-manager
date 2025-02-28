package org.beowolf23.ssh.jsch;

import com.jcraft.jsch.ChannelSftp;
import org.beowolf23.command.FileDownloader;
import org.beowolf23.pool.ManagedConnectionPool;
import org.beowolf23.ssh.SSHConfiguration;
import org.beowolf23.ssh.exception.SSHFileTransferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.function.Function;

public class JschFileDownloader extends FileDownloader<SSHConfiguration, JschConnection> {
    private static final Logger logger = LoggerFactory.getLogger(JschFileDownloader.class);

    public JschFileDownloader(ManagedConnectionPool<SSHConfiguration, JschConnection> pool) {
        super(pool);
    }

    @Override
    public Function<JschConnection, OutputStream> fileToBeDownloaded(String remoteFilePath) {
        return connection -> {
            try {
                ChannelSftp channel = (ChannelSftp) connection.getSession().openChannel("sftp");
                channel.connect();

                OutputStream outputStream = new ByteArrayOutputStream();
                channel.get(remoteFilePath, outputStream);
                channel.disconnect();

                logger.info("File downloaded from {}", remoteFilePath);
                return outputStream;
            } catch (Exception e) {
                logger.error("Download failed: {}", remoteFilePath, e);
                throw new SSHFileTransferException("Download failed: " + remoteFilePath, e);
            }
        };
    }
}