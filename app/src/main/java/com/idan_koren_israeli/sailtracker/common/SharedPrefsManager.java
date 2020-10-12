package com.idan_koren_israeli.sailtracker.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

public class SharedPrefsManager {

    public interface KEYS {
        String CURRENT_USER_PHONE = "current_user_phone";
        String WATCH_EVENT_ID = "watch_event_id";
    }


    private static SharedPrefsManager instance;
    private SharedPreferences prefs;

    public static SharedPrefsManager getInstance() {
        return instance;
    }

    private SharedPrefsManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences("APP_SP_DB", Context.MODE_PRIVATE);
    }

    private SharedPrefsManager(Context context, String sharePreferencesName) {
        prefs = context.getApplicationContext().getSharedPreferences(sharePreferencesName, Context.MODE_PRIVATE);
    }

    public static SharedPrefsManager initHelper(Context context) {
        if (instance == null)
            instance = new SharedPrefsManager(context);
        return instance;
    }

    public static SharedPrefsManager initHelper(Context context, String sharePreferencesName) {
        if (instance == null)
            instance = new SharedPrefsManager(context, sharePreferencesName);
        return instance;
    }

    public void putBoolean(String KEY, boolean value) {
        prefs.edit().putBoolean(KEY, value).apply();
    }

    public void putString(String KEY, String value) {
        prefs.edit().putString(KEY, value).apply();
    }

    public void putObject(String KEY, Object value) {
        prefs.edit().putString(KEY, new Gson().toJson(value)).apply();
    }

    public void putInt(String KEY, int value) {
        prefs.edit().putInt(KEY, value).apply();
    }

    public void putLong(String KEY, long value) {
        prefs.edit().putLong(KEY, value).apply();
    }

    public void putFloat(String KEY, float value) {
        prefs.edit().putFloat(KEY, value).apply();
    }

    public void putDouble(String KEY, double defValue) {

        putString(KEY, String.valueOf(defValue));
    }

    public boolean getBoolean(String KEY, boolean defvalue) {
        return prefs.getBoolean(KEY, defvalue);
    }

    public String getString(String KEY, String defvalue) {
        return prefs.getString(KEY, defvalue);
    }

    public <T> T getObject(String KEY, Class<T> mModelClass) {
        Object object = null;
        try {
            object = new Gson().fromJson(prefs.getString(KEY, ""), mModelClass);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Primitives.wrap(mModelClass).cast(object);
    }

    public int getInt(String KEY, int defValue) {
        return prefs.getInt(KEY, defValue);
    }

    public long getLong(String KEY, long defValue) {
        return prefs.getLong(KEY, defValue);
    }

    public float getFloat(String KEY, float defValue) {
        return prefs.getFloat(KEY, defValue);
    }

    public double getDouble(String KEY, double defValue) {
        return Double.parseDouble(getString(KEY, String.valueOf(defValue)));
    }

    public void removeKey(String KEY) {
        prefs.edit().remove(KEY).apply();
    }

    public boolean contain(String KEY) {
        return prefs.contains(KEY);
    }

    public void registerChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public <T> void putArray(String KEY, ArrayList<T> array) {
        String json = new Gson().toJson(array);
        Log.i("pttt", json);
        prefs.edit().putString(KEY, json).apply();
    }

    public <T> ArrayList<T> getArray(String KEY, TypeToken typeToken) {
        // type token == new TypeToken<ArrayList<YOUR_CLASS>>() {}
        ArrayList<T> arr = null;
        try {
            arr = new Gson().fromJson(prefs.getString(KEY, ""), typeToken.getType());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return arr;
    }

    public <S, T> void putMap(String KEY, HashMap<S, T> map) {
        String json = new Gson().toJson(map);
        prefs.edit().putString(KEY, json).apply();
    }

    public <S, T> HashMap<S, T> getMap(String KEY, TypeToken typeToken) {
        // getMap(MySharedPreferencesV4.KEYS.SP_PLAYLISTS, new TypeToken<HashMap<String, Playlist>>() {});
        // type token == new TypeToken<ArrayList<YOUR_CLASS>>() {}
        HashMap<S, T> map = null;
        try {
            map = new Gson().fromJson(prefs.getString(KEY, ""), typeToken.getType());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
}
