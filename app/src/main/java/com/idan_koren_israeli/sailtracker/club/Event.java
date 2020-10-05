package com.idan_koren_israeli.sailtracker.club;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.idan_koren_israeli.sailtracker.club.exceptions.AlreadyRegisteredException;
import com.idan_koren_israeli.sailtracker.club.exceptions.EventFullException;
import com.idan_koren_israeli.sailtracker.club.exceptions.NotEnoughPointsException;
import com.idan_koren_israeli.sailtracker.firebase.EventDataManager;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

/**
 * A Club event can be a sail/social event/meeting/announcement event/etc...
 * The Sailing club has many events that can be changed from one month to another
 * Managers are able to add new events to the calendar in @CalendarActivity
 */

public class Event implements Serializable {
    private String eid; // Unique ID
    private String name;
    private String description;
    private EventType type;

    private long startTime; // In millis from 1970
    private int minutes; // Length of event

    private final String EVENT_FULL_MESSAGE =  "Member could not be added, "+ getName()+" event is full";
    private final String ALREADY_REGISTERED = "Member is already registered to "  + getName();

    private int price; // Price (in points) for a single participant register
    private ArrayList<String> registeredMembers;
    private int maxMembersCount; // 0 for inf.


    public Event(){
    }


    public Event(String name, String description,
                 EventType type, long startTime, int minutes,
                 int price, ArrayList<String> registeredMembers, int maxMembersCount) {
        this.eid = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.type = type;
        this.startTime = startTime;
        this.minutes = minutes;
        this.price = price;
        this.registeredMembers = registeredMembers;
        this.maxMembersCount = maxMembersCount;
    }


    public Event(String name, String description,
                 EventType type, long startTime, int minutes,
                 int price, int maxMembersCount) {
        this.eid = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.type = type;
        this.startTime = startTime;
        this.minutes = minutes;
        this.price = price;
        this.registeredMembers = new ArrayList<>();
        this.maxMembersCount = maxMembersCount;
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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ArrayList<String> getRegisteredMembers() {
        if(registeredMembers==null)
            initRegisteredList();
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

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }



    //endregion


    //region Extended Date & Time information (Excluded from Firebase)

    @Exclude
    public DateTime getStartDateTime(){
        return new DateTime(startTime);
    }

    @Exclude
    public DateTime getEndDateTime(){
        return getStartDateTime().plusMinutes(minutes);
    }

    //endregion

    @NonNull
    @Override
    public String toString() {
        return "Event{" +
                "eid='" + eid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", startTime=" + startTime +
                ", minutes=" + minutes +
                ", price=" + price +
                ", registeredMembers=" + registeredMembers +
                ", maxMembersCount=" + maxMembersCount +
                '}';
    }

    public void registerMember(ClubMember member) throws EventFullException, NotEnoughPointsException, AlreadyRegisteredException {
        if(registeredMembers==null)
            initRegisteredList();

        if(registeredMembers.contains(member.getUid())){
            throw new AlreadyRegisteredException(ALREADY_REGISTERED);
        }

        if(maxMembersCount!= 0 && registeredMembers.size() == maxMembersCount)
            throw new EventFullException(EVENT_FULL_MESSAGE);
        registeredMembers.add(member.getUid());
        member.deductPoints(getPrice());

        // updating the database
        MemberDataManager.getInstance().storeMember(member);
        EventDataManager.getInstance().storeEvent(this);
    }

    public void unregisterMember(ClubMember member) {
        if(registeredMembers==null)
            initRegisteredList();

        if(registeredMembers.contains(member.getUid())){
            registeredMembers.remove(member.getUid());
            member.addPoints(getPrice());
            MemberDataManager.getInstance().storeMember(member);
            EventDataManager.getInstance().storeEvent(this);
        }
    }

    private void initRegisteredList(){
        registeredMembers = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return eid.equals(event.eid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eid);
    }
}
