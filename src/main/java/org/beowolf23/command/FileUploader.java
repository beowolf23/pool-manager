package org.beowolf23.command;

import org.beowolf23.pool.ConnectionConfiguration;

import java.io.InputStream;

public abstract class FileUploader<T extends ConnectionConfiguration> {

    public abstract void uploadFile(T t, InputStream inputStream, String remoteFilePath) throws Exception;
}
