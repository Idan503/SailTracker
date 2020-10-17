package com.idan_koren_israeli.sailtracker.notification;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.idan_koren_israeli.sailtracker.activity.CalendarActivity;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.common.SharedPrefsManager;

// Event Watch manager is singleton, only one event can be watched at a certain time
@SuppressLint("StaticFieldLeak")
public class EventWatchManager {
    private static EventWatchManager single_instance = null;
    private Activity callerActivity;
    private EventWatchService watchService;
    private Intent watchIntent;
    private SharedPrefsManager spManager;


    private EventWatchManager(Activity caller){
        callerActivity = caller;
        spManager = SharedPrefsManager.getInstance();
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
        Event currentlyWatch = spManager.getObject(SharedPrefsManager.KEYS.WATCHED_EVENT,Event.class);
        if(currentlyWatch!=null){
            if(currentlyWatch.getStartTime() > CommonUtils.getInstance().getIsraelTimeNowMillis()) {
                // Event already watched is in the future, so user can't watch 2 events at the same time
                CommonUtils.getInstance().showToast("You can only watch one event at a time");
                return;
            }
        }


        watchIntent= new Intent(callerActivity, EventWatchService.class);
        watchIntent.putExtra(EventWatchService.KEYS.EVENT_TO_LISTEN, event);
        callerActivity.startService(watchIntent);

        spManager.putObject(SharedPrefsManager.KEYS.WATCHED_EVENT, event);
    }


    // No need to event parameter because user can only watch one event at a time
    public void stopWatch(Event event){
        if(watchIntent !=null) {
            watchService.stopWatchingEvent(event);
            Log.i("pttt", "Stop watching " + event.getEid());
        }

        spManager.removeKey(SharedPrefsManager.KEYS.WATCHED_EVENT);
    }







    public void destroyService(){
        // Service will restart as it will stop, for future notification
        callerActivity.stopService(watchIntent);
    }

}
