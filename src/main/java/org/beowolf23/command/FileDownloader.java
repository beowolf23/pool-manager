package org.beowolf23.command;

import org.beowolf23.pool.ConnectionConfiguration;

import java.io.OutputStream;

public abstract class FileDownloader<T extends ConnectionConfiguration> {

    public abstract OutputStream downloadFile(T t, String remoteFilePath) throws Exception;
}
