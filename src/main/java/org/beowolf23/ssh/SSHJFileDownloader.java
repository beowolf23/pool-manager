package org.beowolf23.ssh;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.OpenMode;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.SFTPEngine;
import org.beowolf23.command.FileDownloader;
import org.beowolf23.pool.ManagedConnection;
import org.beowolf23.pool.ManagedConnectionPool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.function.Function;

public class SSHJFileDownloader extends FileDownloader<SSHJConfiguration, SSHJConnection> {

    public SSHJFileDownloader(ManagedConnectionPool<SSHJConfiguration, SSHJConnection> pool) {
        super(pool);
    }

    @Override
    public Function<SSHJConnection, OutputStream> fileToBeDownloaded(String remoteFilePath) throws Exception {
        return sshjConnection -> {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try (SSHClient client = sshjConnection.getClient();
                 SFTPEngine engine = client.newSFTPClient().getSFTPEngine();
                 RemoteFile file = engine.open(remoteFilePath, EnumSet.of(OpenMode.READ));
                 InputStream is = file.new RemoteFileInputStream()) {
                is.transferTo(os);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return os;
        };
    }

}
