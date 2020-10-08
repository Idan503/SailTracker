package com.idan_koren_israeli.sailtracker.view_holder;

import android.animation.LayoutTransition;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
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
    private MaterialButton infoToggleButton;
    private LinearLayout outerInfoLayout; // For enabling card expend animation
    private boolean showDate;
    
    private String moreInfo, lessInfo;

    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
        findViews();
        setListeners();
    }

    public EventViewHolder(@NonNull View itemView, boolean showingDate) {
        super(itemView);
        this.showDate = showingDate;
        findViews();
        setListeners();
    }


    public void setEventContent(Event event){
        this.event = event;
        nameText.setText(event.getName());
        timeOfDayText.setText(generateTimeString(event));
        descriptionText.setText(event.getDescription());
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
        infoToggleButton = itemView.findViewById(R.id.event_item_BTN_toggle_info);
        outerInfoLayout = itemView.findViewById(R.id.event_item_LAY_outer_info);
        if(showDate) {
            dateText = itemView.findViewById(R.id.event_item_LBL_date);
        }

        moreInfo = itemView.getResources().getString(R.string.event_card_more_info);
        lessInfo = itemView.getResources().getString(R.string.event_card_less_info);
    }

    private void setListeners(){
        LayoutTransition layoutTransition = outerInfoLayout.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

        infoToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(infoToggleButton.getText().equals(moreInfo)){
                    // description is currently not showing, so we will show it
                    showDescription();
                }
                else
                    hideDescription();
            }
        });
    }


    private void showDescription(){
        infoToggleButton.setText(lessInfo);
        descriptionText.setVisibility(View.VISIBLE);
        Log.i("pttt", "SHOW DESC");
    }

    private void hideDescription(){
        infoToggleButton.setText(moreInfo);
        descriptionText.setVisibility(View.GONE);
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
