package com.idan_koren_israeli.sailtracker.my_sails;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.EventType;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;

/**
 * Shows an event without extra functionality (no option to register/unregister)
 * This is only for information providing, no actions
 *
 */
public class EventInfoViewHolder extends RecyclerView.ViewHolder {

    private interface IMAGE_ID_KEYS{
        int FREE_EVENT = R.drawable.ic_launcher_foreground;
        int GUIDED_SAIL = R.drawable.ic_launcher_background;
        int MEMBERS_SAIL = R.drawable.ic_profile_default;
    }

    protected Event event;
    private TextView nameText, descriptionText, timeText, registerStatusText;
    private ImageView image;

    public EventInfoViewHolder(@NonNull View itemView) {
        super(itemView);
        findViews();
    }

    public void setEventContent(Event event){
        this.event = event;
        nameText.setText(event.getName());
        descriptionText.setText(event.getDescription());
        timeText.setText(generateTimeString(event));
        registerStatusText.setText(generateRegisterStatusString(event));
        setPicture(event.getType());
    }

    private void findViews(){
        nameText = itemView.findViewById(R.id.event_item_LBL_name);
        descriptionText = itemView.findViewById(R.id.event_item_LBL_description);
        timeText = itemView.findViewById(R.id.event_item_LBL_time);
        image = itemView.findViewById(R.id.event_item_IMG_image);
        registerStatusText = itemView.findViewById(R.id.event_item_LBL_register_status);
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
