package com.idan_koren_israeli.sailtracker.adapter;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.comparator.SortByStartTime;
import com.idan_koren_israeli.sailtracker.recycler.view_holder.MessageViewHolder;

import org.joda.time.DateTime;

import java.util.List;


/**
 * Shows events' card items that are separated between titles of "past" and "future"
 * This is used in HistoryActivity to show history of past and future sails and events
 *
 * Holds events cards, with 2 extra items for "past" and "future" messages labels
 */
public class SeparatedEventRecyclerAdapter extends EventRecyclerAdapter {

    private int futureTitlePosition; // The item position of the "Future" title

    public SeparatedEventRecyclerAdapter(Context context, List<Event> events) {
        super(context, events,true);
        calculateFutureLabelPosition(events);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position==0){
            //its the past title
            MessageViewHolder pastMessageHolder = (MessageViewHolder) holder;
            pastMessageHolder.setText(context.getResources().getText(R.string.label_past_events));
        }
        else if(position==futureTitlePosition){
            //its the future title
            MessageViewHolder futureMessageHolder = (MessageViewHolder) holder;
            futureMessageHolder.setText(context.getResources().getText(R.string.label_future_events));
        }
        else{
            //its a regular item of event list
            super.onBindViewHolder(holder,convertToListPosition(position));
        }

    }

    private void calculateFutureLabelPosition(List<Event> events) {
        futureTitlePosition = 1; // "past" title is 0

        long now = DateTime.now().getMillis();
        events.sort(new SortByStartTime());
        for (Event event : events) {
            if (event.getStartTime() > now) {
                break;
            }
            futureTitlePosition++; // current event is part of the past
        }
    }


    @Override
    public int getItemCount() {
        return super.getItemCount() + 2; // 2 titles (separates the ryc)
    }

    // de-converting from position of 'separated' to position in the events list
    private int convertToListPosition(int position){
        int listPosition = position-1; //past title is always above
        if(position > futureTitlePosition)
            listPosition-=1;
        return listPosition;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0)
            return ViewType.TITLE;
        if(position==futureTitlePosition)
            return ViewType.TITLE;
        return super.getItemViewType(position);
    }

    public int getFutureTitlePosition(){
        return futureTitlePosition;
    }
}
