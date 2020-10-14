package com.idan_koren_israeli.sailtracker.adapter;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.recycler.listener.OnEventClickedListener;
import com.idan_koren_israeli.sailtracker.recycler.view_holder.RegistrableEventViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Showing today's and future events in user's calendar
 * User can only register to today's and future events.
 */
public class RegistrableEventRecyclerAdapter extends EventRecyclerAdapter {

    protected List<Event> registeredEvents; // Events that user viewing is already registered to


    private OnEventClickedListener onRegisterPress;
    private OnEventClickedListener onUnregisterPress;
    private OnEventClickedListener onWatchClicked, onUnwatchClicked;


    public RegistrableEventRecyclerAdapter(Context context, List<Event> events, List<Event> registered){
        super(context,events,false); // register type recyclerviews not showing dates
        this.registeredEvents = registered;
    }

    public void setButtonsListeners(OnEventClickedListener register, OnEventClickedListener unregister,
                                    OnEventClickedListener watch, OnEventClickedListener unwatch){
        this.onRegisterPress = register;
        this.onUnregisterPress = unregister;
        this.onWatchClicked = watch;
        this.onUnwatchClicked = unwatch;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType== ViewType.EVENT) {
            view = inflater.inflate(R.layout.recycler_registrable_event_item, parent, false);
            return new RegistrableEventViewHolder(view);
        }
        return super.onCreateViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position==0 && noEvents){
            super.onBindViewHolder(holder, position);
        }
        else {
            //Log.i("pttt", "EVENTS BIND: ");
            //Log.i("pttt", "Binding event named " + eventsList.get(position).getName() + " With " + eventsList.get(position).getRegisteredMembersNonNull().size());
            RegistrableEventViewHolder eventHolder = (RegistrableEventViewHolder) holder;
            if (position < eventsList.size()) {
                Event event = eventsList.get(position);
                eventHolder.setEventContent(eventsList.get(position));
                eventHolder.setButtonListener(onRegisterPress, onUnregisterPress, onWatchClicked, onUnwatchClicked);

                if (registeredEvents.contains(event)) {
                    // member is already registered
                    eventHolder.setIsRegistered(true);
                }
                else
                    eventHolder.setIsRegistered(false);
            }
        }
    }

    public void setEventAsUnregistered(Event unregisteredEvent){
        this.registeredEvents.remove(unregisteredEvent);
    }

    public void setEventAsRegistered(Event unregisteredEvent){
        this.registeredEvents.remove(unregisteredEvent);
    }

    public void setRegisteredEvents(List<Event> registered){
        registeredEvents.clear();
        registeredEvents.addAll(registered);
    }
}
