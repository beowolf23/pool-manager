package org.beowolf23.command;

import org.beowolf23.pool.ConnectionConfiguration;

import java.io.InputStream;

public interface FileUploader<T extends ConnectionConfiguration> {

    void uploadFile(T t, InputStream inputStream, String remoteFilePath) throws Exception;
}
