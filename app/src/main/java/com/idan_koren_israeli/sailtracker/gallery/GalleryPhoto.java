package com.idan_koren_israeli.sailtracker.gallery;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * After getting the photos' uris from Firebase Storage, we can save them as a gallery photo
 * This will reduce the number of calls to the cloud database significantly.
 */
public class GalleryPhoto implements Serializable {
    private Uri uri;
    private long timeCreated;

    public GalleryPhoto() {
    }

    public GalleryPhoto(Uri uri, long createdTime) {
        this.uri = uri;
        this.timeCreated = createdTime;

        Log.i("pttt" , this.toString());
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    @NonNull
    @Override
    public String toString(){
        return timeCreated + ": " + uri;
    }
}
