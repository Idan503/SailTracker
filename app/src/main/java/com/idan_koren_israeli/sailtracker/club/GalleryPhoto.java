package com.idan_koren_israeli.sailtracker.club;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * After getting the photos' uris from Firebase Storage, we can save them as a gallery photo
 * This will reduce the number of calls to the cloud database significantly.
 */
public class GalleryPhoto implements Serializable {
    private String url;
    private long timeCreated;

    public GalleryPhoto() {
    }

    public GalleryPhoto(String url, long createdTime) {
        this.url = url;
        this.timeCreated = createdTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
        return timeCreated + ": " + url;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GalleryPhoto other = (GalleryPhoto) obj;
        return this.timeCreated == other.timeCreated;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, timeCreated);
    }
}
