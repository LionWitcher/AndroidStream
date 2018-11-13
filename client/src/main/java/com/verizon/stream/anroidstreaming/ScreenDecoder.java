package com.verizon.stream.anroidstreaming;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ScreenDecoder extends Thread {

//    private MediaCodec mDecoder;
    private Surface mSurface;

    private static final int DEFAULT_BITRATE = 8 * 1000 * 1000;
    private static final int DEFAULT_FRAME_RATE = 60; // fps
    private static final int DEFAULT_I_FRAME_INTERVAL = 10; // seconds

    private boolean mConfigured;
    private boolean mRunning = true;
    private long mTimeoutUs = 10000;

    public ScreenDecoder(Surface surface) {
        mSurface = surface;
        try {
            mCodec = MediaCodec.createDecoderByType("video/avc");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create codec", e);
        }
    }

//    synchronized void configure(Surface surface, int width, int height, ByteBuffer csd0) throws IOException {
//        if (mConfigured) { // просто флаг, чтобы знать, что декодер готов
//            throw new IllegalStateException();
//        }
//        // создаем видео формат
//        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
//        // передаем наш csd-0
//        format.setByteBuffer("csd-0", csd0);
//        format.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_VIDEO_AVC);
//        format.setInteger(MediaFormat.KEY_BIT_RATE, 8000000);
//        format.setInteger(MediaFormat.KEY_FRAME_RATE, 60);
//        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
//        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10);
//
////        videoFormat.setByteBuffer("csd-0", ByteBuffer.wrap(H264VideoUtil.DEFAULT_SPS));
////        videoFormat.setByteBuffer("csd-1", ByteBuffer.wrap(H264VideoUtil.DEFAULT_PPS));
////        /*default*/ static final byte[] DEFAULT_SPS = {0x00, 0x00, 0x00, 0x01, 0x67, 0x64, 0x00, 0x1E, (byte)0xAD, (byte)0x84, 0x01, 0x0C, 0x20, 0x08, 0x61,
////                0x00, 0x43, 0x08, 0x02, 0x18, 0x40, 0x10, (byte)0xC2, 0x00, (byte)0x84, 0x2B, 0x50, 0x50, 0x16, (byte)0xC8};
////        /*default*/ static final byte[] DEFAULT_PPS = {0x00, 0x00, 0x00, 0x01, 0x68, (byte)0xEE, 0x3C, (byte)0xB0};
//
//
//        // создаем декодер
//        mDecoder = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
//        // конфигурируем декодер
//        mDecoder.configure(format, surface, null, 0);
//        mDecoder.start();
//        mConfigured = true;
//    }
//
//    void decodeSample(byte[] data, int offset, int size, long presentationTimeUs, int flags) {
//        if (mConfigured) {
//            // вызов блокирующий
//            int index = mDecoder.dequeueInputBuffer(mTimeoutUs);
//            if (index >= 0) {
//                ByteBuffer buffer = mDecoder.getInputBuffer(index);
//                buffer.clear(); // обязательно сбросить позицию и размер буфера
//                buffer.put(data, offset, size);
//                // сообщаем системе о доступности буфера данных
//                mDecoder.queueInputBuffer(index, 0, size, presentationTimeUs, flags);
//            }
//        }
//    }
//
//    @Override
//    public void run() {
//        try {
//            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo(); // переиспользуем BufferInfo
//            while (mRunning) {
//                if (mConfigured) { // если кодек готов
//                    int index = mDecoder.dequeueOutputBuffer(info, mTimeoutUs);
//                    if (index >= 0) { // буфер с индексом index доступен
//                        // info.size > 0: если буфер не нулевого размера, то рендерим на Surface
//                        mDecoder.releaseOutputBuffer(index, info.size > 0);
//                        // заканчиваем работу декодера если достигнут конец потока данных
//                        if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
//                            mRunning = false;
//                            break;
//                        }
//                    }
//                } else {
//                    // просто спим, т.к. кодек не готов
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException ignore) {
//                    }
//                }
//            }
//        } finally {
//            // освобождение ресурсов
//            release();
//        }
//    }
//
//    void release() {
//        if (mConfigured) {
//            mDecoder.stop();
//            mDecoder.release();
//        }
//    }


    /*
    *
    * */

    private MediaCodec mCodec;

    public void configure(Surface surface, int width, int height, ByteBuffer csd0) {
        String VIDEO_FORMAT = "video/avc";
        if (mConfigured) {
            throw new IllegalStateException("Decoder is already configured");
        }
        MediaFormat format = MediaFormat.createVideoFormat(VIDEO_FORMAT, width, height);
        // little tricky here, csd-0 is required in order to configure the codec properly
        // it is basically the first sample from encoder with flag: BUFFER_FLAG_CODEC_CONFIG
        final byte[] DEFAULT_SPS = {0x00, 0x00, 0x00, 0x01, 0x67, 0x64, 0x00, 0x1E, (byte)0xAD, (byte)0x84, 0x01, 0x0C, 0x20, 0x08, 0x61,
                0x00, 0x43, 0x08, 0x02, 0x18, 0x40, 0x10, (byte)0xC2, 0x00, (byte)0x84, 0x2B, 0x50, 0x50, 0x16, (byte)0xC8};
        final byte[] DEFAULT_PPS = {0x00, 0x00, 0x00, 0x01, 0x68, (byte)0xEE, 0x3C, (byte)0xB0};

        final byte[] DEFAULT_SPS_custom = {0x00, 0x00, 0x00, 0x01, 0x67, 0x42, 0x00, 0x1E, (byte)0xAD, (byte)0x84, 0x01, 0x0C, 0x20, 0x08, 0x61,
                0x00, 0x43, 0x08, 0x02, 0x18, 0x40, 0x10, (byte)0xC2, 0x00, (byte)0x84, 0x2B, 0x50, 0x50, 0x16, (byte)0xC8};

//        format.setByteBuffer("csd-0", ByteBuffer.wrap(DEFAULT_SPS));
//        format.setByteBuffer("csd-1", ByteBuffer.wrap(DEFAULT_PPS));
        format.setByteBuffer("csd-0", csd0);
//        try {
//            mCodec = MediaCodec.createDecoderByType(VIDEO_FORMAT);
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to create codec", e);
//        }
        mCodec.configure(format, surface, null, 0);
        mCodec.start();
        mConfigured = true;
    }

    @SuppressWarnings("deprecation")
    public void decodeSample(byte[] data, int offset, int size, long presentationTimeUs, int flags) {
        mBuffer = ByteBuffer.wrap(data);
        if (mConfigured && mRunning) {
//        if (mRunning) {
            int index = mCodec.dequeueInputBuffer(mTimeoutUs);
            if (index >= 0) {
                ByteBuffer buffer;
                // since API 21 we have new API to use
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    buffer = mCodec.getInputBuffers()[index];
                    buffer.clear();
                } else {
                    buffer = mCodec.getInputBuffer(index);
                }
                if (buffer != null) {
                    buffer.put(data, offset, size);
                    mCodec.queueInputBuffer(index, 0, size, presentationTimeUs, flags);
                }
            }
            return;
        }
        if (!mConfigured) {
            if (mBuffer.capacity() == 27) {
                Log.d("ScreedDecoder", "buffer: " + Arrays.toString(mBuffer.array()));
                Log.d("ScreedDecoder", "flags: " + info.flags);
                configure(
                        mSurface,
                        1080,
                        1920,
                        mBuffer
                );
            }
        }
    }

    private ByteBuffer mBuffer;
    private MediaCodec.BufferInfo info;

    @Override
    public void run() {
        try {
//            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            info = new MediaCodec.BufferInfo();
            info.set();
            while (mRunning) {
                if (mConfigured) {
                    int index = mCodec.dequeueOutputBuffer(info, mTimeoutUs);
                    if (index >= 0) {
                        // setting true is telling system to render frame onto Surface
                        mCodec.releaseOutputBuffer(index, true);
                        if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                            break;
                        }
                    }
                } else {
                    if (info.flags != 0) {
                        Log.d("ScreedDecoder", "flags: " + info.flags);
                    }
                    if (info.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                        // this is the first and only config sample, which contains information about codec
                        // like H.264, that let's configure the decoder
                        Log.e("ScreedDecoder", "Configure: " + Arrays.toString(mBuffer.array()));
                        configure(
                                mSurface,
                                1080,
                                1920,
                                ByteBuffer.wrap(mBuffer.array(), 0, info.size)
                        );
                    }
//                    // just waiting to be configured, then decode and render
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException ignore) {
//                    }
                }
            }
        } finally {
            if (mConfigured) {
                mCodec.stop();
                mCodec.release();
            }
        }
    }
}
