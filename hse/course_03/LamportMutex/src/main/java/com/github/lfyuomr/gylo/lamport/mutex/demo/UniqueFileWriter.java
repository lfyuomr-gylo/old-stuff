package com.github.lfyuomr.gylo.lamport.mutex.demo;

import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

class UniqueFileWriter implements Closeable {
    private @Nullable RandomAccessFile file;
    private @Nullable FileChannel channel;
    private @Nullable FileLock lock;

    UniqueFileWriter(File fileName) throws IOException, IllegalMonitorStateException {
        file = null;
        channel = null;
        lock = null;
        try {
            file = new RandomAccessFile(fileName, "rw");
            channel = file.getChannel();
            lock = channel.tryLock();
            if (lock == null) {
                throw new IllegalMonitorStateException("Failed to acquire file lock. It seems like another" +
                        "process has already locked file.");
            }
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    void append(String text) throws IOException {
        if (channel != null) {
            channel.position(channel.size()).write(ByteBuffer.wrap(text.getBytes()));
        } else {
            throw new ClosedChannelException();
        }
    }

    @Override
    public void close() throws IOException {
        if (lock != null) {
            lock.close();
        }

        if (channel != null) {
            channel.close();
        }

        if (file != null) {
            file.close();
        }
    }
}
