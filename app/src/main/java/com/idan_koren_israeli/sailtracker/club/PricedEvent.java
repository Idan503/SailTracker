package com.idan_koren_israeli.sailtracker.club;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Minutes;

public class PricedEvent extends Event{

    private int normalPrice; // in points
    private int weekendPrice;

    public PricedEvent(){

    }

    public PricedEvent(String name, String description, DateTime start, Minutes length, int price) {
        super(name, description, start, length);
        this.normalPrice = price;
        this.weekendPrice = price;
    }

    public PricedEvent(String name, String description, DateTime start, Minutes length, int normalPrice, int weekendPrice) {
        super(name, description, start, length);
        this.normalPrice = normalPrice;
        this.weekendPrice = weekendPrice;
    }

    //region Getters & Setters

    public int getNormalPrice() {
        return normalPrice;
    }

    public void setNormalPrice(int normalPrice) {
        this.normalPrice = normalPrice;
    }

    public int getWeekendPrice() {
        return weekendPrice;
    }

    public void setWeekendPrice(int weekendPrice) {
        this.weekendPrice = weekendPrice;
    }

    //endregion

    public int getPrice(){
        int dayOfWeek = getStartTime().getDayOfWeek();
        if(dayOfWeek == DateTimeConstants.FRIDAY || dayOfWeek==DateTimeConstants.SATURDAY){
            return weekendPrice;
        }
        return normalPrice;
    }
}
