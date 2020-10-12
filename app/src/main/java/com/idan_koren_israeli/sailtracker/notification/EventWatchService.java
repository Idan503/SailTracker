package com.idan_koren_israeli.sailtracker.notification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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
    private Event eventWatching;
    private SharedPrefsManager sp;

    public interface KEYS{
        String EVENT_TO_LISTEN = "event_to_listen";
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Event toListen = (Event) intent.getSerializableExtra(KEYS.EVENT_TO_LISTEN);
        sp = SharedPrefsManager.initHelper(getApplicationContext());
        eventData = EventDataManager.initHelper();

        if(toListen!=null) {
            eventWatching = toListen;
            valueListener = eventData.watchEventChanges(toListen, onEventChanged);
        }

        return Service.START_STICKY;
    }

    private OnEventLoadedListener onEventChanged = new OnEventLoadedListener() {
        @Override
        public void onEventLoaded(Event event) {
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
        eventData.unwatchEventChanges(eventWatching, valueListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}