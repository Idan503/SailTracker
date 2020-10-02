package com.idan_koren_israeli.sailtracker.calendar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;

public class EventViewHolder extends RecyclerView.ViewHolder {
    private TextView name, description, time;
    private ImageView image;
    private MaterialButton purchase;

    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
        findViews();
    }

    public void setEventContent(Event event){
        name.setText(event.getName());
        description.setText(event.getDescription());
        time.setText(generateTimeString(event));
        CommonUtils.getInstance().setImageResource(image, event.getPictureUri());
    }

    public void setOnClickListener(View.OnClickListener listener){
        purchase.setOnClickListener(listener);
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
        purchase = itemView.findViewById(R.id.event_item_BTN_purchase);
    }


}
