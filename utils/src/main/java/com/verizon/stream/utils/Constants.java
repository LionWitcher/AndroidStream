package com.verizon.stream.utils;

public final class Constants {
    public static final long DEFAULT_BIT_RATE = 8 *1000 * 1000; // 8Mbps
    public static final long DEFAULT_PORT = 27183;
    public static final long DEFAULT_MAX_SIZE = 0; // unlimited
    public static final int RAW_BUFFER_SIZE = 1024;

    // connectivity params
    public static final String SERVER_HOSTNAME = "127.0.0.1";
//    public static final String SERVER_HOSTNAME = "192.168.54.166";
    public static final int SERVER_PORT = 8000;

    private Constants() {
    }
}
