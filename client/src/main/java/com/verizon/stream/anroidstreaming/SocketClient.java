package com.verizon.stream.anroidstreaming;

import com.verizon.stream.utils.LOG;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class SocketClient extends Thread implements Closeable {
    private static final String LOG_TAG = "SocketClient";
    private Socket mSocket;
    private InetSocketAddress mInetSocketAddress;
    private Listener mDataListener;

    public SocketClient(String hostname, int port, Listener listener) {
        mSocket = new Socket();
        mInetSocketAddress = new InetSocketAddress(hostname, port);
        mDataListener = listener;
    }

    @Override
    public void run() {
        try {
            mSocket.connect(mInetSocketAddress);
            LOG.i(LOG_TAG, mSocket.toString());
            while (mSocket.isConnected()) {
                if (mSocket.getInputStream().available() > 0) {
                    ByteBuffer buffer = readBuffer();
                    if (buffer.capacity() > 0) {
                        if (mDataListener != null) {
                            mDataListener.OnDataReceived(buffer);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOG.e(LOG_TAG, "Client connection problem: " + e.getMessage(), e);
        }
    }

    public ByteBuffer readBuffer() throws IOException {
        InputStream is = mSocket.getInputStream();
        DataInputStream dis = new DataInputStream(is);
        byte[] rawBuffer = new byte[is.available()];
        dis.read(rawBuffer);
        ByteBuffer buffer = ByteBuffer.wrap(rawBuffer);
        LOG.e(LOG_TAG, String.format("byteBuffer:%s", buffer.toString()));
        return buffer;
    }

    @Override
    public void close() throws IOException {
        mSocket.shutdownOutput();
        mSocket.shutdownInput();
        mSocket.close();
    }

    interface Listener {
        void OnDataReceived(ByteBuffer byteBuffer);
    }
}
