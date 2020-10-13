package com.idan_koren_israeli.sailtracker.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.idan_koren_israeli.sailtracker.club.Event;

/**
 * Will launch our notification service when phone is starting,
 */
public class EventBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Broadcast listen, service try to stop (app is being killed) or phone start up
        Log.i("pttt"," RESTART");
        context.startService(new Intent(context, EventWatchService.class));
    }
}
