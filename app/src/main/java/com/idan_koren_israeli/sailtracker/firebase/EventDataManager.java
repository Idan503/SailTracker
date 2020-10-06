package com.idan_koren_israeli.sailtracker.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.idan_koren_israeli.sailtracker.club.exception.AlreadyRegisteredException;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.exception.EventFullException;
import com.idan_koren_israeli.sailtracker.club.exception.NotEnoughPointsException;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnListLoadedListener;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnEventLoadedListener;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;

/**
 *
 * Using Firebase Realtime Database to manage club's events.
 *
 *
 */
public class EventDataManager {
    private static EventDataManager single_instance = null;

    private DatabaseReference dbRealtime;

    interface KEYS {
        String EVENTS = "events";
        String DATE_TO_EVENTS = "date_to_events";
        String MEMBER_TO_EVENTS = "member_to_events";
        String MEMBER_TO_NEXT_EVENT = "member_to_next_event";
        String SAIL_MEMBERS_LIST = "registeredMembers";
    }

    private EventDataManager(){
        dbRealtime = FirebaseDatabase.getInstance().getReference();
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
        dbRealtime.child(KEYS.EVENTS).child(event.getEid()).setValue(event);
        dbRealtime.child(KEYS.DATE_TO_EVENTS).child(generateDateStamp(event.getStartDateTime().toLocalDate())).child(event.getEid()).setValue(event.getName());
    }

    // Adds a member to its event by uid
    public void registerMember(final ClubMember member, final Event event) throws EventFullException, NotEnoughPointsException, AlreadyRegisteredException {
        event.registerMember(member);

        // Updating the stored event object with the new member registered in the list
        dbRealtime.child(KEYS.EVENTS).child(event.getEid())
                .child(KEYS.SAIL_MEMBERS_LIST).setValue(event.getRegisteredMembers());


        //region Add Event to Member's List
        ValueEventListener onMembersEventsListLoaded = new ValueEventListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> registeredEvents;
                if(snapshot.getValue()!=null) {
                    registeredEvents = (ArrayList<String>) snapshot.getValue();

                }
                else
                    registeredEvents = new ArrayList<>();
                registeredEvents.add(event.getEid());

                storeMemberRegisteredEventsList(member, registeredEvents);
                // Adding the new event to the list and re-saving it

                updateNextEvent(member.getUid(), registeredEvents);
                // Updating the value of next event
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbRealtime.child(KEYS.MEMBER_TO_EVENTS).child(member.getUid()).addListenerForSingleValueEvent(onMembersEventsListLoaded);
        //endregion


    }


    private void storeNextEvent(final String memberUid, Event newNextEvent){
        dbRealtime.child(KEYS.MEMBER_TO_NEXT_EVENT).child(memberUid).setValue(newNextEvent.getEid());
    }

    private void storeMemberRegisteredEventsList(ClubMember member, ArrayList<String> list){
        dbRealtime.child(KEYS.MEMBER_TO_EVENTS).child(member.getUid()).setValue(list);
    }

    // Loads all events ids that a single member is registered to
    public void loadRegisteredEvents(final ClubMember member, final OnListLoadedListener<Event> onLoaded){
        ValueEventListener onDataLoaded = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> eventsIds = new ArrayList<>();
                for(DataSnapshot child : snapshot.getChildren()){
                    eventsIds.add(child.getValue(String.class));
                }
                loadEventsById(eventsIds,onLoaded);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbRealtime.child(KEYS.MEMBER_TO_EVENTS).child(member.getUid()).addListenerForSingleValueEvent(onDataLoaded);
    }

    // Removes a member from an event that he was registered to
    public void unregisterMember(final ClubMember member, final Event event){
        event.unregisterMember(member);

        // Updating the stored event object
        dbRealtime.child(KEYS.EVENTS).child(generateDateStamp(event.getStartDateTime().toLocalDate())).child(event.getEid())
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

                storeMemberRegisteredEventsList(member,registeredEvents);
                updateNextEvent(member.getUid(),registeredEvents);
                //Saving list of event itself and updating

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbRealtime.child(KEYS.MEMBER_TO_EVENTS).child(member.getUid()).addListenerForSingleValueEvent(onMembersEventsListLoaded);

    }

    // "Next event" is the future event with the closest time to now
    private void updateNextEvent(final String memberUid, ArrayList<String> usersEventsIds){

        OnListLoadedListener<Event> onEventsLoaded = new OnListLoadedListener<Event>() {
            @Override
            public void onListLoaded(ArrayList<Event> list) {
                Event nextEvent = null;
                long nextEventTime = Long.MAX_VALUE;
                for(Event event : list){
                    //iterating on the list to find the next event that is closest to current time
                    if(event.getStartDateTime().getMillis() > DateTime.now().getMillis()
                        && event.getStartDateTime().getMillis() < nextEventTime){
                        // a new "next event" was found"
                        nextEvent = event;
                        nextEventTime = event.getStartTime();
                    }
                }
                if(nextEvent!=null)
                    storeNextEvent(memberUid,nextEvent);
            }
        };

        loadEventsById(usersEventsIds,onEventsLoaded);

    }

    // Loads all events from a single day
    public void loadEventsByDate(LocalDate day, final OnListLoadedListener<Event> onLoaded){

        final OnListLoadedListener<String> onIdsLoaded = new OnListLoadedListener<String>() {
            @Override
            public void onListLoaded(ArrayList<String> list) {
                loadEventsById(list, onLoaded);
            }
        };


        dbRealtime.child(KEYS.DATE_TO_EVENTS).child(generateDateStamp(day)).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> events = new ArrayList<>();
                        for(DataSnapshot child : snapshot.getChildren()){
                            events.add(child.getKey());
                        }
                        onIdsLoaded.onListLoaded(events);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println(error.getMessage());

                    }
                }
        );
    }

    public void loadEventsById(final ArrayList<String> eventsIdsToLoad, final OnListLoadedListener<Event> listener){
        ValueEventListener onDataLoaded = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Event> events = new ArrayList<>();
                for(DataSnapshot child : snapshot.getChildren()){
                    if(eventsIdsToLoad.contains(child.getKey()))
                        events.add(child.getValue(Event.class));
                }
                listener.onListLoaded(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbRealtime.child(KEYS.EVENTS).addValueEventListener(onDataLoaded);
        // Needs to load every time there is an update - registration is shared realtime among all users
    }


    public void loadEventById(final String eventId, final OnEventLoadedListener listener){
        ValueEventListener onDataLoaded = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event loadedEvent;
                for(DataSnapshot child : snapshot.getChildren()){
                    if(eventId.equals(child.getKey())) {
                        loadedEvent = child.getValue(Event.class);
                        listener.onEventLoaded(loadedEvent); //found
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbRealtime.child(KEYS.EVENTS).addValueEventListener(onDataLoaded);
        // Needs to load every time there is an update - registration is shared realtime among all users
    }

    public void loadNextEvent(final ClubMember member, final OnEventLoadedListener onLoaded){

        ValueEventListener onValueLoaded = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nextEventId = snapshot.getValue(String.class);
                if(nextEventId!=null)
                    loadEventById(nextEventId, onLoaded);
                else
                    onLoaded.onEventLoaded(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbRealtime.child(KEYS.MEMBER_TO_NEXT_EVENT).child(member.getUid()).addListenerForSingleValueEvent(onValueLoaded);
    }

    private String generateDateStamp(LocalDate time){
        return time.toString("dd_MM_YYYY");
    }

    // Overloading - no member parameter applies to the current user's ClubMember
    public void loadRegisteredEvents(final OnListLoadedListener<Event> listener){
        loadRegisteredEvents(MemberDataManager.getInstance().getCurrentUser(), listener);
    }

}
