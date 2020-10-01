package com.idan_koren_israeli.sailtracker.calendar;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import java.util.List;

public class EventsRecyclerAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private List<Event> data;
    private LayoutInflater inflater;

    // Should add purchase click listener here

    EventsRecyclerAdapter(Context context,List<Event> events){
        this.inflater = LayoutInflater.from(context);
        this.data = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_event_item,parent,false);
        EventViewHolder viewHolder = new EventViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.setContent(data.get(position));
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public Event getItem(int position){
        return data.get(position);
    }

}
