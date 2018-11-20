package com.verizon.stream.server;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.verizon.stream.utils.Ln;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            java.lang.Process root = Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }
//        runCommandWait("pm grant " + getPackageName() + " android.permission.ACCESS_SURFACE_FLINGER", false);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String[] permissions = new String[]{
                "android.permission.INJECT_EVENTS",
                "android.permission.ACCESS_MOCK_LOCATION",
                "android.permission.CAPTURE_VIDEO_OUTPUT",
                "android.permission.ACCESS_SURFACE_FLINGER",
                "android.permission.BIND_SCREENING_SERVICE",
                "android.Manifest.permission.GRANT_RUNTIME_PERMISSIONS"};
        requestPermissions(permissions, 0);

        for (String perm : permissions) {
            Log.e("MainActivity", "permission: " + perm + " " + checkSelfPermission(perm));
        }


//        String[] args = new String[]{"0", "8000000"};
//        final Options options = createOptions(args);
//        Ln.i("Options: " + options);
//
//        try {
//            scrcpy(options);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("MainActivity", String.format("requestCode: %d, resultCode: %d, data: %s", requestCode, resultCode, data));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("MainActivity", String.format("requestCode: %d, permissions: %s, grants: %s", requestCode, Arrays.toString(permissions), Arrays.toString(grantResults)));

        String[] args = new String[]{"0", "8000000"};
        final Options options = createOptions(args);
        Ln.i("Options: " + options);

        try {
            scrcpy(options);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        Ln.i("Options: " + Arrays.toString(argsx));

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

    // Подсобная функция, которая просто выполняет shell-команду
    static public boolean runCommandWait(String cmd, boolean needsu) {
        try {
            String su = "sh";
            if (needsu) {
                su = "su";
            }

            java.lang.Process process = Runtime.getRuntime().exec(new String[]{su, "-c", cmd});
            int result = process.waitFor();

            return (result == 0);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
