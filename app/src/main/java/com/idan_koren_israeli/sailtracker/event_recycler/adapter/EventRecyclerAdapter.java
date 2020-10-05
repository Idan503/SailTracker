package com.idan_koren_israeli.sailtracker.event_recycler.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.event_recycler.view_holder.RegistrableEventViewHolder;

import java.util.List;


/**
 * This adapter controls the events items that will be shown to users every day on the calendar
 * Holds non clickable items that show information about events
 *
 */
public class EventRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected interface VIEW_TYPE{
        int EVENT = 0;
        int ADD_BUTTON = 1;
        int MESSAGE = 2;
    }

    protected List<Event> eventsList;
    protected LayoutInflater inflater;



    public EventRecyclerAdapter(Context context, List<Event> registered){
        this.inflater = LayoutInflater.from(context);
        this.eventsList = registered;
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
        return VIEW_TYPE.EVENT;
    }
}
