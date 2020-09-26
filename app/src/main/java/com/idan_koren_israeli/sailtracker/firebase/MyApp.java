package com.idan_koren_israeli.sailtracker.firebase;

import android.app.Application;

import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.common.DatabaseManager;
import com.idan_koren_israeli.sailtracker.common.SharedPrefsManager;

public class MyApp extends Application {
    @Override
    public void onCreate(){
    super.onCreate();

    // Initiating Singleton using Application Context only.
    CommonUtils.initHelper(this);
    SharedPrefsManager.initHelper(this);
    DatabaseManager.initHelper();


    }

}
