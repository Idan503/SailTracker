package com.idan_koren_israeli.sailtracker.notification;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.ValueEventListener;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.common.SharedPrefsManager;
import com.idan_koren_israeli.sailtracker.firebase.EventDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnEventLoadedListener;


/**
 * Watching an event meaning listen to changes.
 * When there is a free space (Event is not full) the watch user will get push notification.
 *
 * The push notification will be sent immediately after the other user unregister from the same event.
 *
 */
public class EventWatchService extends Service {

    private ValueEventListener valueListener; // to stop listen after push notification sent
    private EventDataManager eventData;
    private SharedPrefsManager sp;
    private boolean preventRestart = false;

    public interface KEYS{
        String EVENT_TO_LISTEN = "event_to_listen";
        String IS_RESTART_CALL = "is_restart";
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        Log.i("pttt","onStartCommand Service");
        Event toListen = (Event) intent.getSerializableExtra(KEYS.EVENT_TO_LISTEN);
        boolean isRestart = intent.getBooleanExtra(KEYS.IS_RESTART_CALL, false);
        sp = SharedPrefsManager.initHelper(getApplicationContext());
        eventData = EventDataManager.initHelper();

        if(toListen!=null) {
            valueListener = eventData.watchEventChanges(toListen, onEventChanged);
        }


        return Service.START_STICKY;
    }

    private OnEventLoadedListener onEventChanged = new OnEventLoadedListener() {
        @Override
        public void onEventLoaded(Event event) {
            Log.i("pttt", event.getRegisteredMembers().size() + " | " + event.getMaxMembersCount());
            if(event.getRegisteredMembers().size() < event.getMaxMembersCount()){
                // There is a free slot in the event
                showEventAvailableNotification();
                eventData.unwatchEventChanges(event, valueListener);
                sp.removeKey(SharedPrefsManager.KEYS.WATCH_EVENT_ID);
                stopSelf();
            }
        }
    };

    private void showEventAvailableNotification(){
        EventNotificationManager notification = EventNotificationManager.initHelper(this);
        notification.showNotification();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent(this, EventBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}