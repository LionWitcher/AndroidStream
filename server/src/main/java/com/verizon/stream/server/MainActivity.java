package com.verizon.stream.server;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.verizon.stream.utils.Ln;

import java.io.IOException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] args = new String[]{"0", "8000000"};
        final Options options = createOptions(args);
        Ln.i("Options: " + options);

        try {
            scrcpy(options);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Options options = createOptions();
//        try {
//            scrcpy(options);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        makeAppSystem("com.verizon.stream.server");
//        checkPermissions();
//        try {
//            socketServer.writeByteBuffer(ByteBuffer.wrap(new byte[1]));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static void main(String... args) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Ln.e("Exception on thread " + t, e);
            }
        });
        String[] argsx = new String[]{"0", "8000000"};
        final Options options = createOptions(argsx);
        Ln.i("Options: " + options);

        try {
            scrcpy(options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void scrcpy(Options options) throws IOException {
        final Device device = new Device(options);
//        boolean tunnelForward = options.isTunnelForward();
//        boolean tunnelForward = false;
        try {
            DesktopConnection connection = DesktopConnection.open(device);
            ScreenEncoder screenEncoder = new ScreenEncoder(options.getBitRate());

            // asynchronous
            startEventController(device, connection);

            // synchronous
            screenEncoder.streamScreen(device, connection);
        } catch (IOException e) {
            // this is expected on close
            Ln.d("Screen streaming stopped");
        }
    }

    private static void startEventController(final Device device, final DesktopConnection connection) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new EventController(device, connection).control();
                } catch (IOException e) {
                    // this is expected on close
                    Ln.d("Event controller stopped");
                }
            }
        }).start();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private static Options createOptions(String... args) {
        Options options = new Options();
        if (args.length < 1) {
            return options;
        }
        int maxSize = Integer.parseInt(args[0]) & ~7; // multiple of 8
        options.setMaxSize(maxSize);

        if (args.length < 2) {
            return options;
        }
        int bitRate = Integer.parseInt(args[1]);
        options.setBitRate(bitRate);

        if (args.length < 3) {
            return options;
        }
        // use "adb forward" instead of "adb tunnel"? (so the server must listen)
        boolean tunnelForward = Boolean.parseBoolean(args[2]);
        options.setTunnelForward(tunnelForward);

        if (args.length < 4) {
            return options;
        }
        Rect crop = parseCrop(args[3]);
        options.setCrop(crop);

        return options;
    }

    private static Rect parseCrop(String crop) {
        if (crop.isEmpty()) {
            return null;
        }
        // input format: "width:height:x:y"
        String[] tokens = crop.split(":");
        if (tokens.length != 4) {
            throw new IllegalArgumentException("Crop must contains 4 values separated by colons: \"" + crop + "\"");
        }
        int width = Integer.parseInt(tokens[0]);
        int height = Integer.parseInt(tokens[1]);
        int x = Integer.parseInt(tokens[2]);
        int y = Integer.parseInt(tokens[3]);
        return new Rect(x, y, x + width, y + height);
    }
}
