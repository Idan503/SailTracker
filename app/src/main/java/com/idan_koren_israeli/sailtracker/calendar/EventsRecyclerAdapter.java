package com.idan_koren_israeli.sailtracker.calendar;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;

import java.util.ArrayList;
import java.util.List;


/**
 * This adapter controls the events items that will be shown to users every day on the calendar
 *
 */
public class EventsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //region Inner ViewsTypes IDs
    protected static final int EVENT = 0;
    protected static final int ADD_BUTTON = 1;
    //endregion

    protected List<Event> eventsList;
    protected List<Event> registeredEvents; // Events that user viewing is already registered to
    protected LayoutInflater inflater;

    private OnEventClickedListener onRegisterPress;
    private OnEventClickedListener onUnregisterPress;



    public EventsRecyclerAdapter(Context context,List<Event> events, List<Event> registered){
        this.inflater = LayoutInflater.from(context);
        this.eventsList = events;
        this.registeredEvents = registered;
    }

    public void setButtonsListeners(OnEventClickedListener register, OnEventClickedListener unregister){
        this.onRegisterPress = register;
        this.onUnregisterPress = unregister;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(R.layout.recycler_event_item,parent,false);
        return new RegistrableEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RegistrableEventViewHolder eventHolder = new RegistrableEventViewHolder(holder.itemView);
        if(position < eventsList.size()) {
            Event event = eventsList.get(position);
            eventHolder.setEventContent(eventsList.get(position));
            eventHolder.setButtonListener(onRegisterPress, onUnregisterPress);

            Log.i("pttt", "Registered : " + registeredEvents);
            Log.i("pttt", "All : " + eventsList);
            if(registeredEvents.contains(event)){
                // member is already registered
                eventHolder.setIsRegistered(true);
            }
        }

    }


    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public Event getItem(int position){
        return eventsList.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if(eventsList.get(position).getClass() == Event.class)
            return EVENT;
        return EVENT;
    }
}
