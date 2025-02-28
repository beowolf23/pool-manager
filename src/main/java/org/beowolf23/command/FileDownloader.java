package org.beowolf23.command;

import org.beowolf23.pool.Configuration;
import org.beowolf23.pool.Connection;
import org.beowolf23.pool.ManagedConnectionPool;

import java.io.OutputStream;
import java.util.function.Function;

public abstract class FileDownloader<T extends Configuration, V extends Connection> {

    ManagedConnectionPool<T, V> pool;

    public FileDownloader(ManagedConnectionPool<T, V> pool) {
        this.pool = pool;
    }

    public OutputStream downloadFile(T t, String remoteFilePath) throws Exception {
        V connection = pool.borrowObject(t);
        OutputStream outputStream = fileToBeDownloaded(remoteFilePath).apply(connection);
        pool.returnObject(t, connection);
        return outputStream;
    }

    public abstract Function<V, OutputStream> fileToBeDownloaded(String remoteFilePath) throws Exception;
}
