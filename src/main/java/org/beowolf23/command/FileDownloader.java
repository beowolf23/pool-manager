package org.beowolf23.command;

import org.beowolf23.pool.ConnectionConfiguration;

import java.io.IOException;
import java.io.OutputStream;

public interface FileDownloader<T extends ConnectionConfiguration> {

    OutputStream downloadFile(T t, String remoteFilePath) throws Exception;
}
