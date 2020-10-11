package com.idan_koren_israeli.sailtracker.common;

import android.app.Application;

import com.idan_koren_israeli.sailtracker.firebase.EventDataManager;
import com.idan_koren_israeli.sailtracker.firebase.LoginManager;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.location.SeaLocationManager;

import net.danlew.android.joda.JodaTimeAndroid;

public class MyApp extends Application {
    @Override
    public void onCreate(){
    super.onCreate();

    // Singleton classes use Application Context only. (Prevent leaks)

    // Initiating Local Managers
    CommonUtils.initHelper(this);
    SharedPrefsManager.initHelper(this);
    SeaLocationManager.initHelper(this);

    // Initiating Firebase Managers
    MemberDataManager.initHelper();
    EventDataManager.initHelper();
    LoginManager.initHelper();

    // Initiating DateTime manager
    JodaTimeAndroid.init(this);


    }

}
