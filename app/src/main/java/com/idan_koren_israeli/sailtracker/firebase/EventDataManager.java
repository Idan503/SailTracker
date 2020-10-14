package com.idan_koren_israeli.sailtracker.firebase;

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
import java.util.List;

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
        String EVENT_MEMBERS_LIST = "registeredMembers";
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


    //region Load, Store, Delete

    // Adds an event to the db
    public void storeEvent(Event event){
        // event id -> event full object
        dbRealtime.child(KEYS.EVENTS).child(event.getEid()).setValue(event);

        //date timestamp -> event id
        String eventTimeStamp = generateDateStamp(event.getStartDateTime().toLocalDate());
        dbRealtime.child(KEYS.DATE_TO_EVENTS).child(eventTimeStamp).child(event.getEid()).setValue(event.getName());
    }

    public void deleteEvent(final Event event) {
        // Un-registering all users from this event that is gonna be deleted
        OnListLoadedListener<ClubMember> onRegisterListLoaded = new OnListLoadedListener<ClubMember>() {
            @Override
            public void onListLoaded(List<ClubMember> list) {
                for(ClubMember registeredMember : list){
                    unregisterMember(registeredMember, event);
                }

                // Now we can delete event itself safely
                //delete from id -> event map
                dbRealtime.child(KEYS.EVENTS).child(event.getEid()).removeValue();

                //delete from date timestamp -> event map
                String eventTimeStamp = generateDateStamp(event.getStartDateTime().toLocalDate());
                dbRealtime.child(KEYS.DATE_TO_EVENTS).child(eventTimeStamp).child(event.getEid()).removeValue();
            }
        };
        MemberDataManager.getInstance().loadMembersList(event.getRegisteredMembersNonNull(), onRegisterListLoaded);
    }


    public void loadEvent(final String eventId, final OnEventLoadedListener listener){
        ValueEventListener onDataLoaded = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event loadedEvent;
                for(DataSnapshot child : snapshot.getChildren()){
                    if(eventId.equals(child.getKey())) {
                        loadedEvent = child.getValue(Event.class);
                        if(listener!=null)
                            listener.onEventLoaded(loadedEvent); //found
                        return;
                    }
                }
                // code gets to here when event id not found
                listener.onEventLoaded(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbRealtime.child(KEYS.EVENTS).addListenerForSingleValueEvent(onDataLoaded);
    }

    //endregion

    //region Member's Unique Next Event

    private void storeNextEvent(final String memberUid, Event newNextEvent){
        dbRealtime.child(KEYS.MEMBER_TO_NEXT_EVENT).child(memberUid).setValue(newNextEvent.getEid());
    }
    private void removeNextEvent(String memberUid) {
        dbRealtime.child(KEYS.MEMBER_TO_NEXT_EVENT).child(memberUid).removeValue();
    }

    // "Next event" is the future event with the closest time to now
    private void updateNextEvent(final String memberUid, ArrayList<String> usersEventsIds){

        OnListLoadedListener<Event> onEventsLoaded = new OnListLoadedListener<Event>() {
            @Override
            public void onListLoaded(List<Event> list) {
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
                else
                    removeNextEvent(memberUid);
            }
        };

        loadEventsList(usersEventsIds,onEventsLoaded);

    }




    public void loadNextEvent(final ClubMember member, final OnEventLoadedListener onLoaded){

        ValueEventListener onValueLoaded = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nextEventId = snapshot.getValue(String.class);
                if(nextEventId!=null)
                    loadEvent(nextEventId, onLoaded);
                else
                    onLoaded.onEventLoaded(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbRealtime.child(KEYS.MEMBER_TO_NEXT_EVENT).child(member.getUid()).addListenerForSingleValueEvent(onValueLoaded);
    }

    //endregion

    //region Register/Unregister Member
    // Adds a member to its event by uid
    public void registerMember(final ClubMember member, final Event event) throws EventFullException, NotEnoughPointsException, AlreadyRegisteredException {
        event.registerMember(member);

        // Updating the stored event object with the new member registered in the list
        dbRealtime.child(KEYS.EVENTS).child(event.getEid())
                .child(KEYS.EVENT_MEMBERS_LIST).setValue(event.getRegisteredMembersNonNull());


        //region Add Event to Member's List
        ValueEventListener onMembersEventsListLoaded = new ValueEventListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> registeredEvents = new ArrayList<String>();
                if(snapshot.getValue()!=null) {
                    for(DataSnapshot child : snapshot.getChildren()){
                        registeredEvents.add(child.getKey());
                    }
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

    // Removes a member from an event that he was registered to
    public void unregisterMember(final ClubMember member, final Event event){
        event.unregisterMember(member);

        ValueEventListener onMembersEventsListLoaded = new ValueEventListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> registeredEvents = new ArrayList<>();
                if(snapshot.getValue()!=null) {
                    for(DataSnapshot child : snapshot.getChildren()){
                        registeredEvents.add(child.getKey());
                    }
                }
                else
                    return; // Member isn't signed to anything yet
                registeredEvents.remove(event.getEid());

                removeEventFromMemberList(member, event);
                updateNextEvent(member.getUid(),registeredEvents);
                //Saving list of event itself and updating

                dbRealtime.child(KEYS.EVENTS).child(event.getEid())
                        .child(KEYS.EVENT_MEMBERS_LIST).setValue(event.getRegisteredMembersNonNull());
                // Updating the stored event object

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbRealtime.child(KEYS.MEMBER_TO_EVENTS).child(member.getUid()).addListenerForSingleValueEvent(onMembersEventsListLoaded);


    }

    private void storeMemberRegisteredEventsList(ClubMember member, ArrayList<String> list){
        for(String eid : list)
            dbRealtime.child(KEYS.MEMBER_TO_EVENTS).child(member.getUid()).child(eid).setValue("");
    }

    private void removeEventFromMemberList(ClubMember member, Event event){
        dbRealtime.child(KEYS.MEMBER_TO_EVENTS).child(member.getUid()).child(event.getEid()).removeValue();
    }

    // Loads all events ids that a single member is registered to
    public void loadRegisteredEvents(final ClubMember member, final OnListLoadedListener<Event> onLoaded){
        ValueEventListener onDataLoaded = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> eventsIds = new ArrayList<>();
                for(DataSnapshot child : snapshot.getChildren()){
                    eventsIds.add(child.getKey());
                }
                loadEventsList(eventsIds,onLoaded);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbRealtime.child(KEYS.MEMBER_TO_EVENTS).child(member.getUid()).addListenerForSingleValueEvent(onDataLoaded);
        // not using single because we want the screen to refresh on all users
    }

    //endregion

    //region Watch Event (Listener That Calls Back Every Time Event Changes)
    // Listening for a single event all changes
    private ValueEventListener watchEventListener; // Temp value of the current listener, to be removed when stop listening
    public void watchEventChanges(Event event, final OnEventLoadedListener onEventChanges){

        watchEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Event loadedEvent = snapshot.getValue(Event.class);
                    onEventChanges.onEventLoaded(loadedEvent);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbRealtime.child(KEYS.EVENTS).child(event.getEid()).addValueEventListener(watchEventListener);
        // Needs to listen to event event after load (because of watching in the bg), this is why we didn't use single value event
    }

    public void unwatchEventChanges(Event event){
        if(watchEventListener!=null)
            dbRealtime.child(KEYS.EVENTS).child(event.getEid()).removeEventListener(watchEventListener);
    }

    //endregion

    //region Load Multiple Events

    // Loads all events from a single day
    public void loadEventsByDate(LocalDate day, final OnListLoadedListener<Event> onLoaded){

        final OnListLoadedListener<String> onIdsLoaded = new OnListLoadedListener<String>() {
            @Override
            public void onListLoaded(List<String> list) {
                loadEventsList(list, onLoaded);
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

    public void loadEventsList(final List<String> eventsIdsToLoad, final OnListLoadedListener<Event> listener){
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

        dbRealtime.child(KEYS.EVENTS).addListenerForSingleValueEvent(onDataLoaded);
    }

    //endregion

    //region Listen to Multiple Events

    private ValueEventListener listenerToListOfEvents; // Temp value of the current listener, to be removed when stop listening
    // Like load but with a listener not just for a single value change event
    public void listenToEventsByDate(LocalDate day, final OnEventLoadedListener onSingleEventChange){

        final OnListLoadedListener<String> onIdsLoaded = new OnListLoadedListener<String>() {
            @Override
            public void onListLoaded(List<String> list) {
                listenToEventsList(list, onSingleEventChange);
            }
        };


        // Getting ids of events and then listening to this list
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


    public void listenToEventsList(final List<String> eventsIdsToListen, final OnEventLoadedListener onEventChange){
        if(eventsIdsToListen.size()==0) {
            onEventChange.onEventLoaded(null);
            return; //Nothing to listen to
        }

        listenerToListOfEvents = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event eventChanged = snapshot.getValue(Event.class);
                onEventChange.onEventLoaded(eventChanged);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        // This will callback each time something is changed in the list (listening to each event)
        for(String eventId : eventsIdsToListen)
            dbRealtime.child(KEYS.EVENTS).child(eventId).addValueEventListener(listenerToListOfEvents);
    }


    public void stopListenToEventsList(final List<String> eventIds)
    {
        if(listenerToListOfEvents!=null) {
            for (String id : eventIds)
                dbRealtime.child(KEYS.EVENTS).child(id).removeEventListener(listenerToListOfEvents);
        }
    }

    //endregion


    private String generateDateStamp(LocalDate time){
        return time.toString("dd_MM_YYYY");
    }

    // Overloading - no member parameter applies to the current user's ClubMember
    public void loadRegisteredEvents(final OnListLoadedListener<Event> listener){
        loadRegisteredEvents(MemberDataManager.getInstance().getCurrentMember(), listener);
    }

}
