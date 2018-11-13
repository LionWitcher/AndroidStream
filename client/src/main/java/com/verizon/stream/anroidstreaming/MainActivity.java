package com.verizon.stream.anroidstreaming;

import android.hardware.display.VirtualDisplay;
import android.media.MediaPlayer;
import android.media.projection.MediaProjection;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.IccOpenLogicalChannelResponse;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.Button;
import android.widget.TextView;

import com.verizon.stream.utils.Constants;
import com.verizon.stream.utils.IO;
import com.verizon.stream.utils.LOG;

import java.net.Socket;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, SocketClient.Listener {
    private static final String LOG_TAG = "MainActivity";
    private TextView mStatusText;
    private Button mConnectButton;

    private ScreenDecoder mDecoder;

    private int mWidth;
    private int mHeight;
    private Surface mSurface;

//    private MediaPlayer mMediaPlayer;

//    private TextureView m_surface;// View that contains the Surface Texture
//    private H264Provider provider;// Object that connects to our server and gets H264 frames
//    private MediaCodec m_codec;// Media decoder
//    private DecodeFramesTask m_frameTask;// AsyncTask that takes H264 frames and uses the decoder to update the Surface Texture

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStatusText = findViewById(R.id.connect_status);
        mConnectButton = findViewById(R.id.button_connect);

//        mConnectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mConnectButton.setText("Stop");
//                connect();
//
//                SurfaceView surfaceView = new SurfaceView(MainActivity.this);
//                surfaceView.getHolder().addCallback(MainActivity.this);
//                setContentView(surfaceView);
//            }
//        });
//        SurfaceView surfaceView = findViewById(R.id.surface);

        SurfaceView surfaceView = new SurfaceView(MainActivity.this);
        surfaceView.getHolder().addCallback(MainActivity.this);
        setContentView(surfaceView);

        mDecoder = new ScreenDecoder(surfaceView.getHolder().getSurface());
        mDecoder.start();
        SocketClient client = new SocketClient(Constants.SERVER_HOSTNAME, Constants.SERVER_PORT, MainActivity.this);
        client.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(LOG_TAG, String.format("surfaceChanged, %s format:%d, width:%d, height:%d", holder, format, width, height));
//        mDecoder.configure(holder.getSurface(), width, height, null);
//        mSurface = holder.getSurface();
//        mWidth = width;
//        mHeight = height;
//        if (mSurface != null) {
//            byte[] csd_info = {0, 0, 0, 1, 103, 100, 0, 40, -84, 52, -59,
//                    1, -32, 17, 31, 120, 11, 80, 16, 16, 31, 0, 0, 3, 3,
//                    -23, 0, 0, -22, 96, -108, 0, 0, 0, 1, 104, -18, 60,
//                    -128};
//            try {
//                mDecoder.configure(mSurface, mWidth, mHeight, ByteBuffer.wrap(csd_info));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void OnDataReceived(ByteBuffer byteBuffer) {
        LOG.e(LOG_TAG, "OnDataReceived: " + byteBuffer);
//        if (bKeyFrame(byteBuffer.array())) {
//            LOG.e(LOG_TAG, "Configuration received: " + Arrays.toString(byteBuffer.array()));
//        }
        mDecoder.decodeSample(byteBuffer.array(), 0, byteBuffer.array().length, 0, 0);
    }

//    private static boolean bKeyFrame(byte[] frameData) {
//        return ( ( (frameData[4] & 0xFF) & 0x0F) == 0x07);
//    }
}

