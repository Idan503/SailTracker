package com.idan_koren_israeli.sailtracker.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.type.LatLng;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;

import java.io.IOException;
import java.util.List;

@SuppressLint("StaticFieldLeak")
public class SeaLocationManager {

    private static SeaLocationManager single_instance = null;
    private Context context; // Using only app. context

    private OnSeaDetectedListener callback;


    private SeaLocationManager(Context context){
        this.context = context;
    }

    public static SeaLocationManager initHelper(Context context){
        single_instance = new SeaLocationManager(context);
        return single_instance;
    }

    public static SeaLocationManager getInstance(){
        return single_instance;
    }


    // Checks if current user is near / in sea region, is so - dispatch device camera
    public void checkLocationNearSea(Activity callerActivity, OnSeaDetectedListener onChecked) {

        this.setOnLocationDetected(onChecked);
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            CommonUtils.getInstance().requestLocationPermission(callerActivity);
            CommonUtils.getInstance().showToast("Please grant location and try again");
            return;
        }
        // Code gets to here when permission granted
        Location currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(currentLocation==null)
            return;

        // We will check 4 close locations (up down left right) from current location
        // and the current real location
        // as a result - if one of them is "sea" on maps, we determine that current location is near sea.

        final float deltaLat = 0.001f;
        final float deltaLong =  0.001f * (float)Math.cos(currentLocation.getLatitude());
        // As estimation -> 111,111 meters = 1 degree of lat
        //               -> 111,111 * cos(lat) = 1 degree of long
        // So our delta here is approximately 100 meters

        Location up = new Location(currentLocation);
        Location down= new Location(currentLocation);
        Location left= new Location(currentLocation);
        Location right= new Location(currentLocation);

        up.setLatitude(up.getLatitude() + deltaLat);
        down.setLatitude(down.getLatitude() - deltaLat);
        left.setLongitude(left.getLongitude() - deltaLong);
        right.setLongitude(left.getLongitude() + deltaLong);



    }

    private void setOnLocationDetected(OnSeaDetectedListener callback){
        this.callback = callback;
    }

    private LocationListener onLocationDetected = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {


        }
    };


    //endregion

}
