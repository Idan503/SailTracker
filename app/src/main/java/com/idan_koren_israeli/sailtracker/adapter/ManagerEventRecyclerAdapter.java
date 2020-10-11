package com.idan_koren_israeli.sailtracker.adapter;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.view_holder.AddEventViewHolder;
import com.idan_koren_israeli.sailtracker.view_holder.EventViewHolder;
import com.idan_koren_israeli.sailtracker.view_holder.RegistrableEventViewHolder;

import java.util.List;

/**
 *
 * Special adapter for list of the events for each days in calendar.
 * The last item in the list will be an "Add Event" button.
 * ManagerEventRecyclerAdapter will be only shown to manager-users only.
 *
 * A certain user is considered a manager iff its uid is in the designated list in the firestore db
 * Method that checks if a user is manager or not is written in @MembersDataManager
 *
 */
public class ManagerEventRecyclerAdapter extends RegistrableEventRecyclerAdapter {


    View.OnClickListener onAddButtonPressed;

    public ManagerEventRecyclerAdapter(Context context, List<Event> events, List<Event> alreadyRegistered) {
        super(context, events, alreadyRegistered);
    }

    public void setAddClickedListener(View.OnClickListener listener){
        this.onAddButtonPressed = listener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case ViewType.ADD_BUTTON:
                // managers can add new events
                view = inflater.inflate(R.layout.recycler_add_event_item, parent, false);
                return new AddEventViewHolder(view);
            case ViewType.EVENT:
                // managers can delete events, so holder will attach the listener
                view = inflater.inflate(R.layout.recycler_registrable_event_item, parent, false);
                Log.i("pttt", "INFRLATE EVENT MANAGER");
                return new RegistrableEventViewHolder(view,true);
            default:
                return super.onCreateViewHolder(parent,viewType);

        }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(noEvents){
            // first view is a message, second view is an add button
            if(position==0)
                super.onBindViewHolder(holder,position);
            else if(position==1){
                AddEventViewHolder eventHolder = (AddEventViewHolder) holder;
                eventHolder.setClickListener(onAddButtonPressed);
            }
        }
        else if(position==eventsList.size()){
            // Last item in the recyclerview (not in the data list), so it's the add button
            AddEventViewHolder eventHolder = (AddEventViewHolder) holder;
            eventHolder.setClickListener(onAddButtonPressed);
        }
        else{
            // a regular item
            super.onBindViewHolder(holder, position);
        }


    }

    @Override
    public int getItemCount() {
        if(noEvents)
            return 2; // manager has message and add button
        return eventsList.size()+1; // manager view has 1 more item (Add Button)
    }

    @Override
    public int getItemViewType(int position) {
        if(noEvents){
            if(position==0)
                return super.getItemViewType(position);
            if(position==1)
                return ViewType.ADD_BUTTON;
        }
        if(position==eventsList.size())
            return ViewType.ADD_BUTTON;
        return super.getItemViewType(position);
    }




}
