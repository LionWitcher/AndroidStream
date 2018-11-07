package com.verizon.stream.utils;

import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class IO {
    private IO() {
        // not instantiable
    }

    public static void writeFully(FileDescriptor fd, ByteBuffer from) throws IOException {
        // ByteBuffer position is not updated as expected by Os.write() on old Android versions, so
        // count the remaining bytes manually.
        // See <https://github.com/Genymobile/scrcpy/issues/291>.
        Ln.i(from.toString());
        int remaining = from.remaining();
        while (remaining > 0) {
            try {
                int w = Os.write(fd, from);
                if (BuildConfig.DEBUG && w < 0) {
                    // w should not be negative, since an exception is thrown on error
                    throw new AssertionError("Os.write() returned a negative value (" + w + ")");
                }
                remaining -= w;
            } catch (ErrnoException e) {
                if (e.errno != OsConstants.EINTR) {
                    throw new IOException(e);
                }
            }
        }
    }

    public static void writeFully(FileDescriptor fd, byte[] buffer, int offset, int len) throws IOException {
        writeFully(fd, ByteBuffer.wrap(buffer, offset, len));
    }

    public static ByteBuffer readFully(FileDescriptor fd, InputStream inputStream) throws IOException {
        int remaining = inputStream.available();
        ByteBuffer byteBuffer = ByteBuffer.allocate(remaining);
        byte[] temp = new byte[remaining];
        while (true) {
            int status = inputStream.read(temp, 0 , 1);
            if (status == -1) {
                break;
            }
            byteBuffer.put(temp);
        }
        return byteBuffer;
    }

    public static byte[] intToByteArray(int value) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }
}
