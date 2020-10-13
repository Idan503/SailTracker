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
    private Event eventWatched;

    public interface KEYS{
        String EVENT_TO_LISTEN = "event_to_listen";
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        eventWatched = (Event) intent.getSerializableExtra(KEYS.EVENT_TO_LISTEN);
        sp = SharedPrefsManager.initHelper(getApplicationContext());
        eventData = EventDataManager.initHelper();

        if(eventWatched!=null) {
            valueListener = eventData.watchEventChanges(eventWatched, onEventChanged);
        }

        return Service.START_STICKY;
    }

    private OnEventLoadedListener onEventChanged = new OnEventLoadedListener() {
        @Override
        public void onEventLoaded(Event event) {
            if(event.getRegisteredMembers().size() < event.getMaxMembersCount()){
                // There is a free slot in the event
                if(eventWatched!=null)
                    showEventAvailableNotification();
                stopWatchingEvent(event);
            }
        }
    };

    private void showEventAvailableNotification(){
        EventNotificationManager notification = EventNotificationManager.initHelper(this);
        notification.showNotification();
    }

    public void stopWatchingEvent(Event event){
        if(eventData!=null)
            eventData.unwatchEventChanges(event, valueListener);
        eventWatched = null; // User does not watch any event
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        // Code gets to here when application is being killed (inconsistent on some phone)
        Intent broadcastIntent = new Intent(this, EventBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
        stopSelf();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}