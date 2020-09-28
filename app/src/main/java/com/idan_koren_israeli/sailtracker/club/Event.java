package com.idan_koren_israeli.sailtracker.club;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;

/**
 * A Club event can be either a social event/meeting/announcement event/etc...
 *
 */

public class Event {
    private String name;
    private String description;

    private DateTime start;
    private Minutes length; //Calculation of end time will be on runtime

    public Event(){
    }

    public Event(String name, String description, DateTime start, Minutes length) {
        this.name = name;
        this.description = description;
        this.start = start;
        this.length = length;
    }

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

    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public Minutes getLength() {
        return length;
    }

    public void setLength(Minutes length) {
        this.length = length;
    }
}
