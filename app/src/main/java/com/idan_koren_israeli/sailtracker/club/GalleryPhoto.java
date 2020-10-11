package com.idan_koren_israeli.sailtracker.club;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * Photos that members take in their events are saved as an object of GalleryPhoto
 * url is saved in Firebase Storage-Databse.
 *
 * Number of calls to db is reduces by naming the images files as the time they have been created in.
 * This way we have one-to-one function between user and photo, because user can't take 2 photos at the same time.
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
