package com.idan_koren_israeli.sailtracker.view_holder;

import android.animation.LayoutTransition;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.enums.EventType;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;

/**
 * Base class for event cards view holders
 *
 * Shows information about a certain event
 * "More Info" button can be pressed, and card will be expanded by animation
 */
public class EventViewHolder extends RecyclerView.ViewHolder {

    private interface IMAGE_ID_KEYS{
        int EVENING_EVENT = R.drawable.img_evening_event;
        int GUIDED_SAIL = R.drawable.img_guided_sail;
        int MEMBERS_SAIL = R.drawable.img_members_sail;
    }

    protected Event event;

    //region Holding Views
    private TextView nameText, descriptionText, timeOfDayText, dateText, registerStatusText;
    private LinearLayout moreInfoLayout;
    private ImageView image;
    private MaterialButton infoToggleButton;
    //endregion


    private CardView outerCardLayout; // For card expand/collapse animations
    private boolean showDateText;
    
    private boolean cardExpended = false; // "More info" starts as hidden

    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
        findViews();
        setMoreInfoListener();
    }


    public void setEventContent(Event event){
        this.event = event;
        nameText.setText(event.getName());
        timeOfDayText.setText(generateTimeString(event));
        descriptionText.setText(event.getDescription());
        registerStatusText.setText(generateRegisterStatusString(event));
        if(showDateText)
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
        outerCardLayout = itemView.findViewById(R.id.event_item_LAY_outer_card);
        dateText = itemView.findViewById(R.id.event_item_LBL_date);
        moreInfoLayout = itemView.findViewById(R.id.event_item_LAY_more_info);


    }


    //region Show/Hide Date Text

    public void setShowDateText(boolean showDateText){
        this.showDateText = showDateText;
        updateDateView();
    }

    private void updateDateView(){
        if(dateText!=null){
            if(showDateText){
                dateText.setVisibility(View.VISIBLE);
            }
            else{
                dateText.setVisibility(View.GONE);
            }
        }
    }

    //endregion

    //region Show/Hide More Info (Card Expend)

    private void setMoreInfoListener(){
        // Enables the expend animation of the card, after pressing "More Info..."
        LayoutTransition layoutTransition = outerCardLayout.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);


        infoToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!cardExpended){
                    showMoreInfo();
                }
                else {
                    hideMoreInfo();
                }
            }
        });
    }


    private void showMoreInfo(){
        String lessInfoMessage = itemView.getResources().getString(R.string.event_card_less_info);
        infoToggleButton.setText(lessInfoMessage);
        moreInfoLayout.setVisibility(View.VISIBLE);
        cardExpended = true;
    }

    private void hideMoreInfo(){
        String moreInfoMessage = itemView.getResources().getString(R.string.event_card_more_info);
        infoToggleButton.setText(moreInfoMessage);
        moreInfoLayout.setVisibility(View.GONE);
        cardExpended = false;
    }

    //endregion





    //region Labels String Generation

    private String generateTimeString(Event event){
        return event.getStartDateTime().toString("HH:mm") +
                        "-" +
                        event.getEndDateTime().toString("HH:mm");

    }

    private String generateRegisterStatusString(Event event) {
        if(event.getMaxMembersCount() == -1){
            return event.getRegisteredMembers().size() + " Registered"; //Unlimited
        }
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
            case EVENING_EVENT:
                common.setImageResource(image, IMAGE_ID_KEYS.EVENING_EVENT);
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
