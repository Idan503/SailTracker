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

    private ArrayList<String> registeredMembers;
    private int maxMembersCount; // 0 for inf.

    public Sail(){
        super();
    }

    public Sail(Event event, int price, int maxParticipants){
        super(event);
    }

    public Sail(String id, String name, String description, DateTime start, Minutes length, int price, ArrayList<String> participantsUid, int maxParticipants) {
        super(id, name, description, start, length);
        this.price = price;
        this.registeredMembers = participantsUid;
        this.maxMembersCount = maxParticipants;
    }

    //region Getters & Setters
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ArrayList<String> getRegisteredMembers() {
        return registeredMembers;
    }

    public void setRegisteredMembers(ArrayList<String> registeredMembers) {
        this.registeredMembers = registeredMembers;
    }

    public int getMaxMembersCount() {
        return maxMembersCount;
    }

    public void setMaxMembersCount(int maxMembersCount) {
        this.maxMembersCount = maxMembersCount;
    }

    //endregion

    public void registerMember(ClubMember member) throws EventFullException{
        if(registeredMembers.size() == maxMembersCount)
            throw new EventFullException(EVENT_FULL_MESSAGE);
        registeredMembers.add(member.getUid());
        member.removePoints(getPrice());
        // need to save member
    }

    public void unregisterMember(ClubMember member) {
        if(registeredMembers.contains(member.getUid())){
            registeredMembers.remove(member.getUid());
            member.addPoints(getPrice());
            // need to save member
        }
    }

}
