package com.idan_koren_israeli.sailtracker.recycler.view_holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.idan_koren_israeli.sailtracker.R;

/**
 * Holder that holds the "Add Event" button's card
 * It will be shown as the last card, in the calendar events list of managers only.
 */
public class AddEventViewHolder extends RecyclerView.ViewHolder {
    private MaterialButton button;

    public AddEventViewHolder(@NonNull View itemView) {
        super(itemView);
        findViews();
    }


    public void setClickListener(View.OnClickListener listener){
        if(button!=null)
            button.setOnClickListener(listener);
    }


    private void findViews(){
        button = itemView.findViewById(R.id.event_add_item_BTN_button);
    }


}
