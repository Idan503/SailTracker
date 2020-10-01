package com.idan_koren_israeli.sailtracker.calendar;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;

import java.util.List;

/**
 *
 * Special adapter for list of the events for each days.
 * The last item in the list will be an "Add Event" button.
 * ManagerEventRecyclerAdapter will be only shown to manager-users
 *
 * A certain user is considered a manager iff its uid is in the designated list in the firestore db
 * Method that checks if a user is manager or not is written in @MembersDataManager
 *
 */
public class ManagerEventRecyclerAdapter extends EventsRecyclerAdapter {

    View.OnClickListener onAddButtonPressed;

    ManagerEventRecyclerAdapter(Context context, List<Event> events) {
        super(context, events);
    }

    public void setAddClickedListener(View.OnClickListener listener){
        this.onAddButtonPressed = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == ADD_BUTTON) {
            view = inflater.inflate(R.layout.recycler_add_event_item, parent, false);
            return new EventAddViewHolder(view);
        }
        else{
            return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position==data.size()){
            // Last item in the recyclerview (not in the data list), so it's the add button
            EventAddViewHolder eventHolder = new EventAddViewHolder(holder.itemView);
            eventHolder.setClickListener(onAddButtonPressed);
        }
        else {
            super.onBindViewHolder(holder,position);
            // Act as a regular user (load today's event items)
        }
    }

    @Override
    public int getItemCount() {
        return data.size()+1; // manager view has 1 more item (Add Button)
    }

    public Event getItem(int position){
        return data.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if(position == data.size())
            return ADD_BUTTON;
        return super.getItemViewType(position);
    }




}
