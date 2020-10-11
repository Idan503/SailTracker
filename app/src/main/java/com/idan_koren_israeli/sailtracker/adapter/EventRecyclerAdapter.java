package com.idan_koren_israeli.sailtracker.adapter;
import android.content.Context;
import android.view.Gravity;
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

    protected interface ViewType {
        int EVENT = 0;
        int ADD_BUTTON = 1;
        int TITLE = 2;
    }

    private static final int NO_EVENTS_LABEL = R.string.label_no_events_calendar;

    protected List<Event> eventsList;
    protected LayoutInflater inflater;
    protected Context context; // Used for loading strings ids from resource
    private boolean showDate;
    protected boolean noEvents;



    public EventRecyclerAdapter(Context context, List<Event> events, boolean showDate){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.eventsList = events;
        this.showDate = showDate;
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
            // When there are no events, the only thing that will be appeared is a message
        }
        switch (viewType){
            case ViewType.EVENT:
                view = inflater.inflate(R.layout.recycler_event_item,parent,false);
                return new EventViewHolder(view);
            case ViewType.TITLE:
                view = inflater.inflate(R.layout.recycler_message_item,parent,false);
                return new MessageViewHolder(view);
            case ViewType.ADD_BUTTON:
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
            messageHolder.setTextGravity(Gravity.CENTER_HORIZONTAL);
        }
        else {
            EventViewHolder eventHolder = (EventViewHolder) holder;
            eventHolder.setShowDateText(showDate);
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
            return ViewType.TITLE;
        return ViewType.EVENT;
    }
}
