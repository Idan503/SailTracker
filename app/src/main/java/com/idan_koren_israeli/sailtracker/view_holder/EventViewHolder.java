package com.idan_koren_israeli.sailtracker.view_holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.enums.EventType;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;

/**
 * Shows an event without extra functionality (no option to register/unregister)
 * This is only for information providing, no actions
 *
 */
public class EventViewHolder extends RecyclerView.ViewHolder {

    private interface IMAGE_ID_KEYS{
        int FREE_EVENT = R.drawable.ic_launcher_foreground;
        int GUIDED_SAIL = R.drawable.ic_launcher_background;
        int MEMBERS_SAIL = R.drawable.blank_profile_photo;
    }

    protected Event event;
    private TextView nameText, descriptionText, timeOfDayText, dateText, registerStatusText;
    private ImageView image;
    private boolean showDate;

    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
        showDate = false;
        findViews();
    }

    public EventViewHolder(@NonNull View itemView, boolean isShowingDate) {
        super(itemView);
        showDate = isShowingDate;
        findViews();
    }

    public void setEventContent(Event event){
        this.event = event;
        nameText.setText(event.getName());
        descriptionText.setText(event.getDescription());
        timeOfDayText.setText(generateTimeString(event));
        registerStatusText.setText(generateRegisterStatusString(event));
        if(showDate)
            dateText.setText(generateDateString(event));
        setPicture(event.getType());
    }

    private void findViews(){
        nameText = itemView.findViewById(R.id.event_item_LBL_name);
        descriptionText = itemView.findViewById(R.id.event_item_LBL_description);
        timeOfDayText = itemView.findViewById(R.id.event_item_LBL_time_of_day);
        image = itemView.findViewById(R.id.event_item_IMG_image);
        registerStatusText = itemView.findViewById(R.id.event_item_LBL_register_status);
        if(showDate)
            dateText = itemView.findViewById(R.id.event_item_LBL_date);
    }



    //region Labels string generation

    private String generateTimeString(Event event){
        return event.getStartDateTime().toString("HH:mm") +
                        "-" +
                        event.getEndDateTime().toString("HH:mm");

    }

    private String generateRegisterStatusString(Event event) {
        return event.getRegisteredMembers().size() + "/"
                        + event.getMaxMembersCount() + " Registered";
    }

    private String generateDateString(Event event){
        return event.getStartDateTime().toString("dd/MM/YYYY");
    }

    //endregion



    private void setPicture(EventType type){
        // setting the image differently for each type
        CommonUtils common = CommonUtils.getInstance();
        switch (type){
            case FREE_EVENT:
                common.setImageResource(image, IMAGE_ID_KEYS.FREE_EVENT);
                break;
            case GUIDED_SAIL:
                common.setImageResource(image, IMAGE_ID_KEYS.GUIDED_SAIL);
                break;
            case MEMBERS_SAIL:
                common.setImageResource(image, IMAGE_ID_KEYS.MEMBERS_SAIL);
                break;
        }
    }

}
