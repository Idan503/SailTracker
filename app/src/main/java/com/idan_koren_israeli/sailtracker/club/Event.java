package com.idan_koren_israeli.sailtracker.club;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

/**
 * A Club event can be either a social event/meeting/announcement event/etc...
 *
 */

public class Event {
    private String name;
    private String description;

    private DateTime startTime;
    private Minutes length; //Calculation of end time will be on runtime

    public Event(){
    }

    public Event(String name, String description, DateTime start, Minutes length) {
        this.name = name;
        this.description = description;
        this.startTime = start;
        this.length = length;
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

    //endregion

    public DateTime getEndTime(){
        return startTime.plusMillis(length.getMinutes());
    }



}
