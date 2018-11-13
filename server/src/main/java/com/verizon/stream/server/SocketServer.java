package com.verizon.stream.server;

import com.verizon.stream.utils.Constants;
import com.verizon.stream.utils.LOG;
import com.verizon.stream.utils.Ln;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SocketServer extends Thread {
    private static final String LOG_TAG = "SocketServer";
    private java.net.ServerSocket mServerSocket;
    private CommunicationThread communicationThread;
    public static final Object sLock = new Object();

    public SocketServer() {
    }

    @Override
    public void run() {
        try {
            startServer();
        } catch (IOException e) {
            LOG.e(LOG_TAG, "Server connection problem: " + e.getMessage(), e);
        }
    }

    private void startServer() throws IOException {
        Socket socket;
        mServerSocket = new java.net.ServerSocket(Constants.SERVER_PORT);
        while (isAlive()) {
            socket = mServerSocket.accept();
            communicationThread = new CommunicationThread(socket);
            new Thread(communicationThread).start();
            LOG.i(LOG_TAG, socket.toString());
        }
    }

    public boolean isAliveComm() {
        return communicationThread != null;
    }

    public void writeBytesArray(byte[] bytesArray) throws IOException {
//        Socket socket = mServerSocket.accept();
//        if (mSocket != null && mSocket.isConnected()) {
//            CommunicationThread commThread = new CommunicationThread(mSocket, bytesArray);
//            new Thread(commThread).start();
//            writeBytesArray(bytesArray, mSocket.getOutputStream());
        if (communicationThread != null)
            communicationThread.setBytes(bytesArray);
//        }
    }

    private void writeBytesArray(byte[] bytesArray, OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.write(bytesArray);
        dos.flush();
    }

    private void writeBuffer(ByteBuffer buffer, OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.write(buffer.array());
        dos.flush();
        LOG.e(LOG_TAG, String.format("temp:%s, byteBuffer:%s",
                Arrays.toString(buffer.array()), buffer.toString()));
    }

    private class CommunicationThread implements Runnable {
        private Socket mClientSocket;
        private byte[] mBytes;

        public CommunicationThread(Socket clientSocket) {
            mClientSocket = clientSocket;
        }

        public void setBytes(byte[] bytes) {
            mBytes = bytes;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted() && mClientSocket.isConnected()) {
                try {
                    if (mBytes != null && !mClientSocket.isOutputShutdown()) {
                        writeBytesArray(mBytes, mClientSocket.getOutputStream());
                        mBytes = null;
                    }
                } catch (IOException e) {
                    Ln.e(e.getMessage(), e);
                    break;
                }
            }
        }
    }
}
