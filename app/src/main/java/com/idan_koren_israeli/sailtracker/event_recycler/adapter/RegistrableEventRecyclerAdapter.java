package com.idan_koren_israeli.sailtracker.event_recycler.adapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.SortByStartTime;
import com.idan_koren_israeli.sailtracker.event_recycler.OnEventClickedListener;
import com.idan_koren_israeli.sailtracker.event_recycler.view_holder.RegistrableEventViewHolder;

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
        view = inflater.inflate(R.layout.recycler_registrable_event_item,parent,false);
        return new RegistrableEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RegistrableEventViewHolder eventHolder = new RegistrableEventViewHolder(holder.itemView);
        if(position < eventsList.size()) {
            Event event = eventsList.get(position);
            eventHolder.setEventContent(eventsList.get(position));
            eventHolder.setButtonListener(onRegisterPress, onUnregisterPress);

            if(registeredEvents.contains(event)){
                // member is already registered
                eventHolder.setIsRegistered(true);
            }
        }

    }

}