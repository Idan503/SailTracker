package com.idan_koren_israeli.sailtracker.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.activity.CalendarActivity;

public class EventNotificationManager {
    @SuppressLint("StaticFieldLeak") // singleton class that use application context only
    private static EventNotificationManager single_instance = null;
    private Context context;

    public interface KEYS{
        String CHANNEL_ID ="SAIL_NOTIFICATION_CHANNEL";
        String NAME_WATCHED_AVAILABLE = "WatchedAvailable";
    }

    private interface STRINGS{
        String TEXT_WATCHED_AVAILABLE = "You can now register to a watched event.";
        String TITLE_WATCHED_AVAILABLE = "Someone has unregistered!";
        String ACTION_STRING_WATCHED_AVAILABLE = "Launch Calendar";
    }

    private EventNotificationManager(Context context){
        this.context = context;
    }

    public static EventNotificationManager initHelper(Context context){
        if(single_instance==null)
            single_instance = new EventNotificationManager(context);
        return single_instance;
    }

    public static EventNotificationManager getInstance(){
        return single_instance;
    }

    public void showNotification(){
        Intent intent=new Intent(context.getApplicationContext(), CalendarActivity.class);

        NotificationChannel notificationChannel=new NotificationChannel(KEYS.CHANNEL_ID,KEYS.NAME_WATCHED_AVAILABLE, NotificationManager.IMPORTANCE_HIGH);
        PendingIntent pendingIntent=PendingIntent.getActivity(context.getApplicationContext(),1,intent,0);

        Notification notification=new Notification.Builder(context.getApplicationContext(),KEYS.CHANNEL_ID)
                .setContentText(STRINGS.TEXT_WATCHED_AVAILABLE)
                .setContentTitle(STRINGS.TITLE_WATCHED_AVAILABLE)
                .setContentIntent(pendingIntent)
                .addAction(buildAction(pendingIntent))
                .setChannelId(KEYS.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();

        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(1,notification);
    }

    private Notification.Action buildAction(PendingIntent intent){
        return new Notification.Action.Builder(
                Icon.createWithResource(context, R.drawable.ic_launcher_foreground),
                STRINGS.ACTION_STRING_WATCHED_AVAILABLE,
                intent).build();
    };


}
