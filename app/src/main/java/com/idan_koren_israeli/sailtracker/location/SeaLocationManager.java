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
import java.util.ArrayList;
import java.util.List;

@SuppressLint("StaticFieldLeak")
public class SeaLocationManager {

    private static SeaLocationManager single_instance = null;
    private Context context; // Using only app. context


    // Special case: shavit marin location (Explained later)
    private static final double SHAVIT_LAT = 32.804924;
    private static final double SHAVIT_LONG = 35.031876;
    private static final double SHAVIT_SENSITIVITY = 0.005; // ~ 555 meters

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
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            CommonUtils.getInstance().requestLocationPermission(callerActivity);
            CommonUtils.getInstance().showToast("Please grant location and try again");
            return;
        }
        // Code gets to here when permission granted
        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location==null)
            return;

        LatLng currentLatLng = LatLng.newBuilder()
                .setLatitude(location.getLatitude())
                .setLongitude(location.getLongitude())
                .build();


        // We will check 4 close locations (top bottom left right) from current location
        // and the current real location
        // as a result - if one of them is "sea" on maps, we determine that current location is near sea.

        final float deltaLat = 0.007f;
        final float deltaLong =  0.007f;
        // As estimation -> 111,111 meters = ~ 1 degree of lat/long
        // So our delta here is approximately 700 meters

        List<LatLng> locationsToCheck = generateVicinity(currentLatLng, deltaLat, deltaLong);


        Geocoder geocoder = new Geocoder(context);
        try {
            for(LatLng latLng : locationsToCheck) {
                List<Address> info = geocoder.getFromLocation(latLng.getLatitude(), latLng.getLongitude(), 1);
                if (info.isEmpty()) { // No territory address found - mean its ocean or natural resource
                    onChecked.onSeaDetected(true);
                    return;
                }
                else{
                    Address address = info.get(0);
                    if(address.getAdminArea() == null && address.getSubAdminArea() == null && address.getPostalCode()==null){
                        // All properties of land authority address are null, we can assume its sea.
                        onChecked.onSeaDetected(true);
                        return;
                    }
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

        if(isAroundShavitMarina(currentLatLng)) {
            onChecked.onSeaDetected(true);
        }
        else {
            // All addresses checked are located in land, and location is not around Shavit
            onChecked.onSeaDetected(false);
        }

    }

    // This creates a lat lng points to check around original. if one of them is sea -> original is close to sea
    private List<LatLng> generateVicinity(LatLng original, float deltaLat, float deltaLong){
        LatLng top = generateLatLngOffset(original, deltaLat, 0);
        LatLng bottom = generateLatLngOffset(original, -deltaLat, 0);
        LatLng topLeft = generateLatLngOffset(original, deltaLat, -deltaLong);
        LatLng topRight = generateLatLngOffset(original, deltaLat, deltaLong);
        LatLng left = generateLatLngOffset(original, 0, -deltaLong);
        LatLng right = generateLatLngOffset(original, 0, deltaLong);
        LatLng bottomLeft = generateLatLngOffset(original, -deltaLat, -deltaLong);
        LatLng bottomRight = generateLatLngOffset(original, -deltaLat, deltaLong);


        // now we have 9 location, original (current) one and 8 directions, we will check if on of the is inside sea
        List<LatLng> vicinityLatLng = new ArrayList<>();
        vicinityLatLng.add(original);
        vicinityLatLng.add(top);
        vicinityLatLng.add(left);
        vicinityLatLng.add(right);
        vicinityLatLng.add(bottom);
        vicinityLatLng.add(topLeft);
        vicinityLatLng.add(topRight);
        vicinityLatLng.add(bottomLeft);
        vicinityLatLng.add(bottomRight);

        return vicinityLatLng;
    }

    // In Israel, Shavit Marina is a located in an area which is around land.
    // Therefore, the system cannot detect that is near the sea, this method fixes that
    private boolean isAroundShavitMarina(LatLng currentLocation){
        double latDelta = Math.abs(currentLocation.getLatitude() - SHAVIT_LAT);
        double longDelta = Math.abs(currentLocation.getLongitude() - SHAVIT_LONG);

        return (latDelta < SHAVIT_SENSITIVITY) && (longDelta < SHAVIT_SENSITIVITY);
        // Location is close to shavit

    }

    private LatLng generateLatLngOffset(LatLng source, double deltaLat, double deltaLong){
        return LatLng.newBuilder()
                .setLatitude(source.getLatitude() + deltaLat)
                .setLongitude(source.getLongitude() + deltaLong)
                .build();

    }



    //endregion

}
