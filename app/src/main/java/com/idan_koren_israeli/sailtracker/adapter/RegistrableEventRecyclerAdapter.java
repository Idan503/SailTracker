package com.idan_koren_israeli.sailtracker.adapter;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.comparator.SortByStartTime;
import com.idan_koren_israeli.sailtracker.view_holder.listener.OnEventClickedListener;
import com.idan_koren_israeli.sailtracker.view_holder.RegistrableEventViewHolder;

import java.util.List;


/**
 * This adapter controls the events items that will be shown to users every day on the calendar
 *
 */
public class RegistrableEventRecyclerAdapter extends EventRecyclerAdapter {

    protected List<Event> registeredEvents; // Events that user viewing is already registered to


    private OnEventClickedListener onRegisterPress;
    private OnEventClickedListener onUnregisterPress;


    public RegistrableEventRecyclerAdapter(Context context, List<Event> events, List<Event> registered){
        super(context,events);
        this.registeredEvents = registered;
        eventsList.sort(new SortByStartTime());

    }

    public void setButtonsListeners(OnEventClickedListener register, OnEventClickedListener unregister){
        this.onRegisterPress = register;
        this.onUnregisterPress = unregister;
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
            RegistrableEventViewHolder eventHolder = (RegistrableEventViewHolder) holder;
            if (position < eventsList.size()) {
                Event event = eventsList.get(position);
                eventHolder.setEventContent(eventsList.get(position));
                eventHolder.setRegisterButtonListener(onRegisterPress, onUnregisterPress);

                if (registeredEvents.contains(event)) {
                    // member is already registered
                    eventHolder.setIsRegistered(true);
                }
                else
                    eventHolder.setIsRegistered(false);
            }
        }
    }

}
