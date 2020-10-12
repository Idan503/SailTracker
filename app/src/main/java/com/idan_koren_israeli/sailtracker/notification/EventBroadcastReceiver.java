package com.idan_koren_israeli.sailtracker.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Will launch our notification service when phone is starting,
 */
public class EventBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, EventWatchService.class);

        // needs to add here event to listen to as extra from sp

        context.startService(startServiceIntent);
    }
}
