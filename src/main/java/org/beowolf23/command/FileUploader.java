package org.beowolf23.command;

import org.beowolf23.pool.Connection;
import org.beowolf23.pool.Configuration;
import org.beowolf23.pool.ManagedConnectionPool;

import java.io.InputStream;
import java.util.function.Consumer;

public abstract class FileUploader<T extends Configuration, V extends Connection> {

    ManagedConnectionPool<T, V> pool;

    public FileUploader(ManagedConnectionPool<T, V> pool) {
        this.pool = pool;
    }

    public void uploadFile(T t, InputStream inputStream, String remoteFilePath) throws Exception {
        V connection = pool.borrowObject(t);
        fileToBeUploaded(inputStream, remoteFilePath).accept(connection);
        pool.returnObject(t, connection);
    }

    public abstract Consumer<V> fileToBeUploaded(InputStream inputStream, String remoteFilePath) throws Exception;
}
