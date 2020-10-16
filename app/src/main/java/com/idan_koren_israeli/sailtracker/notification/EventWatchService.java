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
    private Event eventWatched;

    private boolean calledRestart = false;


    public interface KEYS{
        String EVENT_TO_LISTEN = "event_to_listen";
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        eventWatched = (Event) intent.getSerializableExtra(KEYS.EVENT_TO_LISTEN);
        eventData = EventDataManager.initHelper();

        SharedPrefsManager sp = SharedPrefsManager.initHelper(getApplicationContext());
        if(eventWatched==null){
            // eventWatched was not in intent's data, so we check if its saved as sp (when device startup)
            eventWatched = (Event) sp.getObject(SharedPrefsManager.KEYS.WATCHED_EVENT, Event.class);
        }

        if(eventWatched!=null) {
            // Initiating the listening itself iff user is listened
            eventData.watchEventChanges(eventWatched, onEventChanged);
        }

        return Service.START_STICKY; // To prevent the service get killed
    }

    private OnEventLoadedListener onEventChanged = new OnEventLoadedListener() {
        @Override
        public void onEventLoaded(Event event) {
            if(event!=null && event.getRegisteredMembersNonNull().size() < event.getMaxMembersCount()){
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
            eventData.unwatchEventChanges(event);
        eventWatched = null; // User does not watch any event
    }


    //region Destroy Service to Restart (For when app is being killed)

    // Some phones sent to onTaskRemove (chinese brands), while other send to onDestroy...
    // Therefore the boolean calledRestart will prevent both calls.
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if(!calledRestart) {
            // Code gets to here when application is being killed (inconsistent on some devices)
            Intent broadcastIntent = new Intent(this, EventBroadcastReceiver.class);
            sendBroadcast(broadcastIntent);
            stopSelf();
            calledRestart = true;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!calledRestart) {
            // Code gets to here when application is being killed (inconsistent on some devices)
            Intent broadcastIntent = new Intent(this, EventBroadcastReceiver.class);
            sendBroadcast(broadcastIntent);
            stopSelf();
            calledRestart = true;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}