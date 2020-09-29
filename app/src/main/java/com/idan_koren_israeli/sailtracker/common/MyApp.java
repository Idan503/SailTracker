package com.idan_koren_israeli.sailtracker.common;

import android.app.Application;

import com.idan_koren_israeli.sailtracker.firebase.MembersDataManager;

import net.danlew.android.joda.JodaTimeAndroid;

public class MyApp extends Application {
    @Override
    public void onCreate(){
    super.onCreate();

    // Initiating Singleton using Application Context only.
    CommonUtils.initHelper(this);
    SharedPrefsManager.initHelper(this);
    MembersDataManager.initHelper();
    JodaTimeAndroid.init(this);


    }

}
