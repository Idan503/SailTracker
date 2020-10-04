package com.idan_koren_israeli.sailtracker.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.idan_koren_israeli.sailtracker.club.AlreadyRegisteredException;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.EventFullException;
import com.idan_koren_israeli.sailtracker.club.NotEnoughPointsException;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnEventsLoadedListener;
import org.joda.time.LocalDate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * Using Firebase Realtime Database to manage club's events.
 *
 *
 */
public class EventDataManager {
    private static EventDataManager single_instance = null;

    private DatabaseReference database;

    interface KEYS {
        String EVENTS = "events";
        String MEMBER_TO_EVENTS = "member_to_events";
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
    public void registerMember(final ClubMember member, final Event event) throws EventFullException, NotEnoughPointsException, AlreadyRegisteredException {
        event.registerMember(member);

        // Updating the stored event object
        database.child(KEYS.EVENTS).child(generateDateStamp(event.getStartDateTime().toLocalDate())).child(event.getEid())
                .child(KEYS.SAIL_MEMBERS_LIST).setValue(event.getRegisteredMembers());


        ValueEventListener onMembersEventsListLoaded = new ValueEventListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> registeredEvents;
                if(snapshot.getValue()!=null)
                    registeredEvents = (ArrayList<String>) snapshot.getValue();
                else
                    registeredEvents = new ArrayList<>();
                registeredEvents.add(event.getEid());
                database.child(KEYS.MEMBER_TO_EVENTS).child(member.getUid()).setValue(registeredEvents);
                // Adding the new event to the list and re-saving it
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        database.child(KEYS.MEMBER_TO_EVENTS).child(member.getUid()).addListenerForSingleValueEvent(onMembersEventsListLoaded);

    }

    // Removes a member from an event that he was registered to
    public void unregisterMember(final ClubMember member, final Event event){
        event.unregisterMember(member);

        // Updating the stored event object
        database.child(KEYS.EVENTS).child(generateDateStamp(event.getStartDateTime().toLocalDate())).child(event.getEid())
                .child(KEYS.SAIL_MEMBERS_LIST).setValue(event.getRegisteredMembers());


        ValueEventListener onMembersEventsListLoaded = new ValueEventListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> registeredEvents;
                if(snapshot.getValue()!=null)
                    registeredEvents = (ArrayList<String>) snapshot.getValue();
                else
                    return; // Member isn't signed to anything yet
                registeredEvents.remove(event.getEid());
                database.child(KEYS.MEMBER_TO_EVENTS).child(member.getUid()).setValue(registeredEvents);
                // Adding the new event to the list and re-saving it
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        database.child(KEYS.MEMBER_TO_EVENTS).child(member.getUid()).addListenerForSingleValueEvent(onMembersEventsListLoaded);

    }

    // Loads all events from a single day
    public void loadEvents(LocalDate day, final OnEventsLoadedListener onLoaded){
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
        return time.toString("dd_MM_YYYY");
    }


}
