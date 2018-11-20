package com.verizon.stream.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.verizon.stream.utils.LOG;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && !TextUtils.isEmpty(intent.getAction()) &&
                intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            LOG.d(LOG_TAG, "start Stream service");
            context.startService(new Intent(context, StreamService.class));
        }
    }
}
