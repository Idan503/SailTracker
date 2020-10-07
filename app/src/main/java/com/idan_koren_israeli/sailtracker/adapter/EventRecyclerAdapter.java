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
import com.idan_koren_israeli.sailtracker.view_holder.AddEventViewHolder;
import com.idan_koren_israeli.sailtracker.view_holder.EventViewHolder;
import com.idan_koren_israeli.sailtracker.view_holder.MessageViewHolder;

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

    private static final int NO_EVENTS_LABEL = R.string.no_events_calendar;

    protected List<Event> eventsList;
    protected LayoutInflater inflater;
    protected Context context; // Used for loading strings ids from resource
    protected boolean noEvents;



    public EventRecyclerAdapter(Context context, List<Event> events){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.eventsList = events;
        events.sort(new SortByStartTime());
        noEvents = (events.size()==0);
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(noEvents) {
            view = inflater.inflate(R.layout.recycler_message_item, parent, false);
            return new MessageViewHolder(view);
        }
        switch (viewType){
            case VIEW_TYPE.EVENT:
                view = inflater.inflate(R.layout.recycler_event_item,parent,false);
                return new EventViewHolder(view);
            case VIEW_TYPE.TITLE:
                view = inflater.inflate(R.layout.recycler_message_item,parent,false);
                return new MessageViewHolder(view);
            case VIEW_TYPE.ADD_BUTTON:
                view = inflater.inflate(R.layout.recycler_add_event_item,parent,false);
                return new AddEventViewHolder(view);
        }

        // in non of above, return regular view
        view = inflater.inflate(R.layout.recycler_event_item,parent,false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(noEvents && position==0){
            // showing message of "no events"
            MessageViewHolder messageHolder = (MessageViewHolder) holder;
            messageHolder.setText(context.getResources().getText(NO_EVENTS_LABEL));
        }
        else {
            EventViewHolder eventHolder = (EventViewHolder) holder;
            if (position < eventsList.size()) {
                Event event = eventsList.get(position);
                eventHolder.setEventContent(event);
            }
        }
    }

    @Override
    public int getItemCount() {
        if(noEvents)
            return 1;
        return eventsList.size();
    }

    public Event getItem(int position){
        return eventsList.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if(noEvents && position==0)
            return VIEW_TYPE.TITLE;
        return VIEW_TYPE.EVENT;
    }
}
