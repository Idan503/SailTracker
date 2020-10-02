package com.idan_koren_israeli.sailtracker.club;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.ArrayList;

/**
 * Prices event are events that cost points, and there is a track on how many participants are registered
 * All Sails will be allotted as priced events
 *
 */
public class Sail extends Event {

    private int price; // Price (in points) for a single participant register

    private final String EVENT_FULL_MESSAGE =  "Member could not be added, "+ getName()+" event is full";

    private ArrayList<String> participantsUid;
    private int maxParticipants; // 0 for inf.

    public Sail(){
        super();
    }

    public Sail(Event event, int price, int maxParticipants){
        super(event);
    }

    public Sail(String name, String description, DateTime start, Minutes length, int price, ArrayList<String> participantsUid, int maxParticipants) {
        super(name, description, start, length);
        this.price = price;
        this.participantsUid = participantsUid;
        this.maxParticipants = maxParticipants;
    }

    //region Getters & Setters
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ArrayList<String> getParticipantsUid() {
        return participantsUid;
    }

    public void setParticipantsUid(ArrayList<String> participantsUid) {
        this.participantsUid = participantsUid;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    //endregion

    public void addParticipant(ClubMember member) throws EventFullException{
        if(participantsUid.size() == maxParticipants)
            throw new EventFullException(EVENT_FULL_MESSAGE);
        participantsUid.add(member.getUid());
        member.removePoints(getPrice());
    }

}
