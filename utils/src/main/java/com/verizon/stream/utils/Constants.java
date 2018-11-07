package com.verizon.stream.utils;

public final class Constants {

    public static final String ACCESS_SURFACE_FLINGER_PERMISSION = "android.permission.ACCESS_SURFACE_FLINGER";

    public static final String SOCKET_NAME = "scrcpy";
    public static final long DEFAULT_BIT_RATE = 8 *1000 * 1000; // 8Mbps
    public static final long DEFAULT_PORT = 27183;
    public static final long DEFAULT_MAX_SIZE = 0; // unlimited

    public static final String SERVER_HOSTNAME = "192.168.1.0";
    public static final int SERVER_PORT = 8080;

    private Constants() {
    }
}
