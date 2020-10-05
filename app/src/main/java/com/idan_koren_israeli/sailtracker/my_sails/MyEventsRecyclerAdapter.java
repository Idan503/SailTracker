package com.idan_koren_israeli.sailtracker.my_sails;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.calendar.RegistrableEventViewHolder;
import com.idan_koren_israeli.sailtracker.club.Event;

import java.util.ArrayList;
import java.util.List;


/**
 * This adapter controls the events items that will be shown to users every day on the calendar
 *
 */
public class MyEventsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //region Inner ViewsTypes IDs
    protected static final int EVENT = 0;
    //endregion

    protected List<Event> data;
    protected LayoutInflater inflater;
    protected ArrayList<String> registeredEventsIds; // Events that user viewing is already registered to

    private boolean registeredToAll = false;



    public MyEventsRecyclerAdapter(Context context, List<Event> registered){
        this.inflater = LayoutInflater.from(context);
        this.data = registered;
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
        if(position < data.size()) {
            Event event = data.get(position);
            eventHolder.setEventContent(data.get(position));

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
