package com.idan_koren_israeli.sailtracker.calendar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.EventType;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;

public class EventViewHolder extends RecyclerView.ViewHolder {
    private Event event;
    private TextView name, description, time;
    private ImageView image;
    private MaterialButton button;
    private boolean registered;

    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
        image = null;
        registered = false;
        findViews();
    }

    public void setEventContent(Event event){
        this.event = event;
        this.registered = registered;
        name.setText(event.getName());
        description.setText(event.getDescription());
        time.setText(generateTimeString(event));
        setPicture(event.getType());
    }

    private void setPicture(EventType type){
        // setting the image differently for each type
        CommonUtils common = CommonUtils.getInstance();
        switch (type){
            case FREE_EVENT:
                common.setImageResource(image, R.drawable.ic_baseline_add_24);
                break;
            case GUIDED_SAIL:
                common.setImageResource(image, R.drawable.ic_baseline_home_24);
                break;
            case MEMBERS_SAIL:
                common.setImageResource(image, R.drawable.ic_baseline_image_24);
                break;
        }
    }

    public void setButtonListener(final OnEventClickedListener register, final OnEventClickedListener unregister){
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registered)
                    unregister.onButtonClicked(event);
                else
                    register.onButtonClicked(event);
            }
        });
    }

    public void setIsRegistered(boolean isRegistered){
        this.registered = isRegistered;
        button.setCornerRadius(30);
    }

    private String generateTimeString(Event event){
        String result =
                event.getStartDateTime().toString("HH:mm") +
                "-" +
                event.getEndDateTime().toString("HH:mm");
        return result;

    }

    private void findViews(){
        name = itemView.findViewById(R.id.event_item_LBL_name);
        description = itemView.findViewById(R.id.event_item_LBL_description);
        time = itemView.findViewById(R.id.event_item_LBL_time);
        image = itemView.findViewById(R.id.event_item_IMG_image);
        button = itemView.findViewById(R.id.event_item_BTN_purchase);
    }


}
