package com.idan_koren_israeli.sailtracker.club;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.ArrayList;

// In a sail there is a max number of participants
public class Sail extends PricedEvent {

    private ArrayList<String> participantsUid;
    private int maxParticipants; // 0 for inf.

    public Sail(){
        super();
    }

    public Sail(String name, String description, DateTime start, Minutes length, int price, ArrayList<String> participantsUid, int maxParticipants) {
        super(name, description, start, length, price);
        this.participantsUid = participantsUid;
        this.maxParticipants = maxParticipants;
    }

    public Sail(String name, String description, DateTime start, Minutes length, int normalPrice, int weekendPrice, ArrayList<String> participantsUid, int maxParticipants) {
        super(name, description, start, length, normalPrice, weekendPrice);
        this.participantsUid = participantsUid;
        this.maxParticipants = maxParticipants;
    }
}
