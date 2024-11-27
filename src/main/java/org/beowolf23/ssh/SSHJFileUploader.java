package org.beowolf23.ssh;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.OpenMode;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.SFTPEngine;
import org.beowolf23.command.FileUploader;
import org.beowolf23.pool.ManagedConnectionPool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.function.Consumer;

public class SSHJFileUploader extends FileUploader<SSHJConfiguration, SSHJConnection> {

    public SSHJFileUploader(ManagedConnectionPool<SSHJConfiguration, SSHJConnection> pool) {
        super(pool);
    }

    @Override
    public Consumer<SSHJConnection> fileToBeUploaded(InputStream inputStream, String remoteFilePath) throws Exception {
        return sshjConnection -> {
        try (SSHClient client = sshjConnection.getClient();
             SFTPEngine engine = client.newSFTPClient().getSFTPEngine();
             RemoteFile file = engine.open(remoteFilePath, EnumSet.of(OpenMode.CREAT, OpenMode.WRITE));
             OutputStream os = file.new RemoteFileOutputStream(0, 10)) {
            inputStream.transferTo(os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        };
    }
}
