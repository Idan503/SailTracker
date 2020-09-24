package com.idan_koren_israeli.sailtracker.common;

import android.app.Application;

public class MyApp extends Application {
    @Override
    public void onCreate(){
    super.onCreate();

    // Initiating Singletone with Application Context only.
    CommonUtils.initHelper(this);
    SharedPrefsManager.initHelper(this);
    UserDataManager.initHelper(this);


    }

}
