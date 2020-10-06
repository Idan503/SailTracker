package com.idan_koren_israeli.sailtracker.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.view_holder.EventAddViewHolder;

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
public class ManagerEventRecyclerAdapter extends RegistrableEventRecyclerAdapter {


    View.OnClickListener onAddButtonPressed;

    public ManagerEventRecyclerAdapter(Context context, List<Event> events, List<Event> alreadyRegistered) {
        super(context, events, alreadyRegistered);
    }

    public void setAddClickedListener(View.OnClickListener listener){
        this.onAddButtonPressed = listener;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position!= eventsList.size() || (noEvents && position==0)) {
            super.onBindViewHolder(holder, position);
            // Act as a regular user (load today's event items)
        }
        else {
            // Last item in the recyclerview (not in the data list), so it's the add button
            EventAddViewHolder eventHolder = new EventAddViewHolder(holder.itemView);
            eventHolder.setClickListener(onAddButtonPressed);
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
        if(!noEvents && position == eventsList.size())
            return VIEW_TYPE.ADD_BUTTON;
        if(noEvents && position==1)
            return VIEW_TYPE.ADD_BUTTON;
        return super.getItemViewType(position);
    }




}
