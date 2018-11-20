package com.verizon.stream.anroidstreaming;

import android.content.Context;
import android.view.SurfaceHolder;
import android.widget.LinearLayout;

public class SurfaceWindow extends LinearLayout implements SurfaceHolder.Callback {

    public SurfaceWindow(Context context) {
        super(context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
