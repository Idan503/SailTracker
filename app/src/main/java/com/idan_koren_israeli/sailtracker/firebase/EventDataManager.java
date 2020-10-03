package com.idan_koren_israeli.sailtracker.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.EventFullException;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnEventsLoaded;
import com.idan_koren_israeli.sailtracker.club.Event;

import org.joda.time.LocalDate;

import java.util.ArrayList;

public class EventDataManager {
    private static EventDataManager single_instance = null;

    private DatabaseReference database;

    interface KEYS {
        String EVENTS = "events";
        String SAIL_MEMBERS_LIST = "registeredMembers";
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
        database.child(KEYS.EVENTS).child(generateDateStamp(event.getStartDateTime().toLocalDate())).child(event.getEid()).setValue(event);

    }

    // Adds a member to its event by uid
    public void registerMember(ClubMember member, Event event) throws EventFullException {
        event.registerMember(member);

        // Updating the stored event object
        database.child(KEYS.EVENTS).child(generateDateStamp(event.getStartDateTime().toLocalDate())).child(event.getEid())
                .child(KEYS.SAIL_MEMBERS_LIST).setValue(event.getRegisteredMembers());
    }

    // Removes a member from an event that he was registered to
    public void unregisterMember(ClubMember member, Event event){
        event.unregisterMember(member);

        // Updating the stored event object
        database.child(KEYS.EVENTS).child(generateDateStamp(event.getStartDateTime().toLocalDate())).child(event.getEid())
                .child(KEYS.SAIL_MEMBERS_LIST).setValue(event.getRegisteredMembers());


    }

    // Loads all events from a single day
    public void loadEvents(LocalDate day, final OnEventsLoaded onLoaded){
        database.child(KEYS.EVENTS).child(generateDateStamp(day)).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Event> events = new ArrayList<>();
                        for(DataSnapshot child : snapshot.getChildren()){
                            events.add(child.getValue(Event.class));
                        }
                        onLoaded.onEventsListener(events);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println(error.getMessage());

                    }
                }
        );
    }


    private String generateDateStamp(LocalDate time){
        Log.i("pttt" , " Stamp generated : " + time.toString("dd_MM_YYYY"));
        return time.toString("dd_MM_YYYY");
    }


}
