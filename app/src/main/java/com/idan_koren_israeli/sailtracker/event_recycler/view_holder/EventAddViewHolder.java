package com.idan_koren_israeli.sailtracker.event_recycler.view_holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;

public class EventAddViewHolder extends RecyclerView.ViewHolder {
    private MaterialButton button;

    public EventAddViewHolder(@NonNull View itemView) {
        super(itemView);
        findViews();
    }


    public void setClickListener(View.OnClickListener listener){
        button.setOnClickListener(listener);
    }


    private void findViews(){
        button = itemView.findViewById(R.id.event_add_item_BTN_button);
    }


}
