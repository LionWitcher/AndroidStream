package com.verizon.stream.utils;

import android.util.Log;

public class LOG {

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, msg);
//            System.out.println("VERBOSE: " + msg);
        }
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
//            System.out.println("DEBUG: " + msg);
        }
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg);
//            System.out.println("INFO: " + msg);
        }
    }

//    public static void w(String tag, String msg) {
//        if (BuildConfig.DEBUG) Log.w(tag, msg);
//    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
//            System.out.println("ERROR: " + msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg, tr);
//            System.out.println("ERROR: " + msg);
            tr.printStackTrace();
        }
    }

    private LOG() {
    }
}
