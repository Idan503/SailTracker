package com.idan_koren_israeli.sailtracker.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.comparator.SortByStartTime;
import com.idan_koren_israeli.sailtracker.view_holder.EventViewHolder;

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
        int TITLE = 2;
    }

    protected List<Event> eventsList;
    protected LayoutInflater inflater;



    public EventRecyclerAdapter(Context context, List<Event> events){
        this.inflater = LayoutInflater.from(context);
        this.eventsList = events;
        events.sort(new SortByStartTime());
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
        EventViewHolder eventHolder = (EventViewHolder) holder;
        if(position < eventsList.size()) {
            Event event = eventsList.get(position);
            eventHolder.setEventContent(event);
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
