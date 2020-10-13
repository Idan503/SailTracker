package com.idan_koren_israeli.sailtracker.notification;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.idan_koren_israeli.sailtracker.activity.CalendarActivity;
import com.idan_koren_israeli.sailtracker.club.Event;

// Event Watch manager is singleton, only one event can be watched at a certain time
@SuppressLint("StaticFieldLeak")
public class EventWatchManager {
    private static EventWatchManager single_instance = null;
    private Activity callerActivity;
    private EventWatchService watchService;
    private Intent watchIntent;

    private EventWatchManager(Activity caller){
        callerActivity = caller;

        watchService = new EventWatchService();
        watchIntent = new Intent(caller, watchService.getClass());
        if(!isWatchServiceRunning(watchService.getClass())){
            // Service isn't running, so we start it
            caller.startService(watchIntent);
        }
    }

    public static EventWatchManager initHelper(Activity caller){
        single_instance = new EventWatchManager(caller);
        return single_instance;
    }

    public EventWatchManager getInstance(){
        return single_instance;
    }

    // Deprecated method does not effect result:
    // "as of Android O, getRunningServices is no longer available to third party applications.
    // For backwards compatibility, it will still return the caller's own services"
    private boolean isWatchServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) callerActivity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }




    public void startWatch(Event event){
        watchService = new EventWatchService();

        watchIntent= new Intent(callerActivity, EventWatchService.class);
        watchIntent.putExtra(EventWatchService.KEYS.EVENT_TO_LISTEN, event);
        callerActivity.startService(watchIntent);
    }


    // No need to event parameter because user can only watch one event at a time
    public void stopWatch(){
        if(watchIntent !=null)
            callerActivity.stopService(watchIntent);
    }



    public void destroyService(){
        // Stopping the service, while we know it will be restated
        callerActivity.stopService(watchIntent);
    }

}
