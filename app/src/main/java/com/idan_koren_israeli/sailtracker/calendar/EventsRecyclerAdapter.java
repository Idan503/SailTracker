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


/**
 * This adapter controls the events items that will be shown to users every day on the calendar
 *
 */
public class EventsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //region Inner ViewsTypes IDs
    protected static final int EVENT = 0;
    protected static final int SAIL = 1;
    protected static final int ADD_BUTTON = 2;
    //endregion

    protected List<Event> data;
    protected LayoutInflater inflater;

    private View.OnClickListener onPurchasePressed;

    // Should add purchase click listener here


    EventsRecyclerAdapter(Context context,List<Event> events){
        this.inflater = LayoutInflater.from(context);
        this.data = events;
    }

    public void setOnPurchasePressed(View.OnClickListener purchasePressed){
        this.onPurchasePressed = purchasePressed;
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
        Log.i("pttt" , data.get(position).toString());
        if(position < data.size())
            eventHolder.setEventContent(data.get(position));

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
        if(data.get(position).getClass() == Sail.class)
            return SAIL;
        return EVENT;
    }
}
