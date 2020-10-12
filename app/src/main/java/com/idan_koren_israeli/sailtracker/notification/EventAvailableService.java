package com.idan_koren_israeli.sailtracker.notification;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.firestore.core.EventManager;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.firebase.EventDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnEventLoadedListener;

public class EventAvailableService extends Service {

    public interface KEYS{
        String EVENT_TO_LISTEN = "event_to_listen";
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Event toListen = (Event) intent.getSerializableExtra(KEYS.EVENT_TO_LISTEN);
        EventDataManager eventData = EventDataManager.initHelper();

        if(toListen!=null) {
            eventData.listenToEventChanges(toListen, onEventChanged);
        }
        else
            showEventAvailableNotification();

        return Service.START_STICKY;
    }

    private OnEventLoadedListener onEventChanged = new OnEventLoadedListener() {
        @Override
        public void onEventLoaded(Event event) {
            if(event.getRegisteredMembers().size() < event.getMaxMembersCount()){
                // There is a free slot in the event
                showEventAvailableNotification();
                stopSelf();
            }
        }
    };

    private void showEventAvailableNotification(){
        EventNotificationManager notification = EventNotificationManager.initHelper(this);
        notification.showNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}