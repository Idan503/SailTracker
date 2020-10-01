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
import com.idan_koren_israeli.sailtracker.club.Sail;

import java.util.List;


public class EventsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //region Inner views types IDs
    private static final int EVENT = 0;
    private static final int SAIL = 1;
    private static final int ADD_BUTTON = 2;
    //endregion

    private List<Event> data;
    private LayoutInflater inflater;
    private boolean managerViewMode; // when true - last view will be "add event" button

    // Should add purchase click listener here


    EventsRecyclerAdapter(Context context,List<Event> events){
        this.inflater = LayoutInflater.from(context);
        this.data = events;
        this.managerViewMode = false;
    }

    EventsRecyclerAdapter(Context context,List<Event> events, boolean managerView){
        this.inflater = LayoutInflater.from(context);
        this.data = events;
        this.managerViewMode = managerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view;
        switch (viewType){
            case EVENT:
            case SAIL:
                view = inflater.inflate(R.layout.recycler_event_item,parent,false);
                break;
            default:
                view = inflater.inflate(R.layout.recycler_add_event_item,parent,false);
                Log.i("pttt", " Creating last button");
                break;
        }

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position==data.size()){
            // Setting click listener
        }
        else {
            EventViewHolder eventHolder = (EventViewHolder) holder;
            eventHolder.setContent(data.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return managerViewMode ? data.size()+1 : data.size(); // manager view has 1 more item
    }

    public Event getItem(int position){
        return data.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if(managerViewMode)
            if(position == data.size())
                return ADD_BUTTON;
        if(data.get(position).getClass() == Event.class)
            return EVENT;
        if(data.get(position).getClass() == Sail.class)
            return SAIL;
        return EVENT;
    }
}
