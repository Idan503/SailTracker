package com.idan_koren_israeli.sailtracker.adapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.view_holder.EventViewHolder;
import com.idan_koren_israeli.sailtracker.view_holder.MessageViewHolder;

import org.joda.time.DateTime;

import java.util.List;


/**
 * Adapter that holds events that are separated between past and future, with messages of "past" and "future"
 * This is used in HistoryActivity to show history of past and future sails and events
 */
public class SeparatedEventRecyclerAdapter extends EventRecyclerAdapter {

    private int futureTitlePosition; // The item position of the "Future" title

    public SeparatedEventRecyclerAdapter(Context context, List<Event> events) {
        super(context, events);

        futureTitlePosition = 1; // past title is 0

        long now = DateTime.now().getMillis();
        for(Event event : events){
            if(event.getStartTime() > now)
                break;
            futureTitlePosition++; // current event is part of the past
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position==0){
            //its the past title
            MessageViewHolder pastMessageHolder = (MessageViewHolder) holder;
            pastMessageHolder.setText(context.getResources().getText(R.string.past_events));
        }
        else if(position==futureTitlePosition){
            //its the future title
            MessageViewHolder futureMessageHolder = (MessageViewHolder) holder;
            futureMessageHolder.setText(context.getResources().getText(R.string.future_events));
        }
        else{
            //its a regular item of event list
            super.onBindViewHolder(holder,convertToListPosition(position));
        }

    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 2; // 2 titles (separates the ryc)
    }

    // de-converting from position of separated to position in the list
    private int convertToListPosition(int position){
        int listPosition = position-1; //past title
        if(position > futureTitlePosition)
            listPosition-=1;
        return listPosition;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0)
            return VIEW_TYPE.TITLE;
        if(position==futureTitlePosition)
            return VIEW_TYPE.TITLE;
        return super.getItemViewType(position);
    }

    public int getFutureTitlePosition(){
        return futureTitlePosition;
    }
}