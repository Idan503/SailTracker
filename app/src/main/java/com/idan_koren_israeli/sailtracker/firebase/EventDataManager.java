package com.idan_koren_israeli.sailtracker.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.club.Event;

import org.joda.time.DateTime;

public class EventDataManager {
    private static EventDataManager single_instance = null;

    private DatabaseReference database;

    interface KEYS {
        String EVENTS = "events";
    }

    private EventDataManager(){
        database = FirebaseDatabase.getInstance().getReference();
    }

    public static EventDataManager getInstance() {
        return single_instance;
    }

    public static EventDataManager
    initHelper() {
        if (single_instance == null) {
            single_instance = new EventDataManager();
        }
        return single_instance;
    }


    // Adds an event to the db
    public void storeEvent(Event event){

    }

    // Adds a member to its event by uid
    public void addMember(ClubMember member, Event event){

    }

    // Removes a member from an event that he was registered to
    public void removeMember(ClubMember member, Event event){

    }

    // Loads all events from a single day
    public void loadEvents(DateTime day){

    }


}
