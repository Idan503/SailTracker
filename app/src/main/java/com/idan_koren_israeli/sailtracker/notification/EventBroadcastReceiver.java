package com.idan_koren_israeli.sailtracker.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.common.SharedPrefsManager;

/**
 * Will launch our notification service when phone is starting,
 */
public class EventBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // This receiver will get call both from EventWatchService.java
        // And "BOOT_COMPLETED" call for when the phone starts app.

        // Then, the service will start and listen to the event iff user is watching it
        context.startService(new Intent(context, EventWatchService.class));
    }
}
