package com.idan_koren_israeli.sailtracker.club;

import android.net.Uri;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

/**
 * A Club event can be either a social event/meeting/announcement event/etc...
 *
 */

public class Event {
    private String eid; // Unique ID
    private String name;
    private String description;

    private DateTime startTime;
    private Minutes length; //Calculation of end time will be on runtime

    private Uri picture;

    public Event(){
    }

    public Event(Event other){
        this.eid = other.eid;
        this.name = other.name;
        this.description = other.description;
        this.length = other.length;
        this.picture = other.picture;
        this.startTime = other.startTime;
    }

    public Event(String eid, String name, String description, DateTime start, Minutes length) {
        this.eid = eid;
        this.name = name;
        this.description = description;
        this.startTime = start;
        this.length = length;
        this.picture = null;
    }

    public Event(String eid, String name, String description, DateTime startTime, Minutes length, Uri picture) {
        this.eid = eid;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.length = length;
        this.picture = picture;
    }

    //region Getters & Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public Minutes getLength() {
        return length;
    }

    public void setLength(Minutes length) {
        this.length = length;
    }

    public Uri getPictureUri() {
        return picture;
    }

    public void setPictureUri(Uri picture) {
        this.picture = picture;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    //endregion



    public DateTime getEndTime(){
        return startTime.plusMinutes(length.getMinutes());
    }

    @Override
    public String toString() {
        return "Event{" + eid +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", length=" + length +
                ", picture=" + picture +
                '}';
    }
}
