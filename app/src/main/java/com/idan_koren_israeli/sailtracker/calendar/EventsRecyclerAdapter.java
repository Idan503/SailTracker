package com.idan_koren_israeli.sailtracker.calendar;
import android.content.Context;
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

    protected List<Event> data;
    protected LayoutInflater inflater;
    protected ArrayList<String> registeredEventsIds; // Events that user viewing is already registered to

    private OnEventClickedListener onRegisterPress;
    private OnEventClickedListener onUnregisterPress;

    // Should add purchase click listener here


    EventsRecyclerAdapter(Context context,List<Event> events, ArrayList<String> registered){
        this.inflater = LayoutInflater.from(context);
        this.data = events;
        this.registeredEventsIds = registered;
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
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EventViewHolder eventHolder = new EventViewHolder(holder.itemView);
        if(position < data.size()) {
            Event event = data.get(position);
            eventHolder.setEventContent(data.get(position));
            eventHolder.setButtonListener(onRegisterPress, onUnregisterPress);

            if(registeredEventsIds.contains(event.getEid())){
                // member is already registered
                eventHolder.setIsRegistered(true);
            }
        }

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public Event getItem(int position){
        return data.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if(data.get(position).getClass() == Event.class)
            return EVENT;
        return EVENT;
    }
}
