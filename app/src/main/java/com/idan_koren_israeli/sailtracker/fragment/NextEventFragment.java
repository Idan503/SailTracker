package com.idan_koren_israeli.sailtracker.fragment;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.enums.EventType;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.firebase.EventDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnEventLoadedListener;

import org.joda.time.DateTime;

/**
 * This card present the next planned sail that the user purchased
 *
 */
public class NextEventFragment extends Fragment {

    private TextView titleText, dateText, startTimeText;
    private ImageView backgroundImage;
    private Resources resources;
    //private ShimmerFrameLayout shimmer;

    public NextEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_next_event_card, container, false);
        resources = parent.getResources();
        findViews(parent);

        return parent;
    }

    private OnEventLoadedListener onNextEventLoaded = new OnEventLoadedListener() {
        @Override
        public void onEventLoaded(Event sail) {
            if(sail!=null && sail.getStartDateTime().getMillis() > DateTime.now().getMillis())
                setEvent(sail);
            else
                setNoEvent();
            // Data is loaded
        }
    };

    private void findViews(View parent){
        titleText = parent.findViewById(R.id.next_event_LBL_event_name);
        backgroundImage = parent.findViewById(R.id.next_event_IMG_background);
        startTimeText = parent.findViewById(R.id.next_event_LBL_start_time);
        dateText = parent.findViewById(R.id.next_event_LBL_date);
        //shimmer = parent.findViewById(R.id.next_event_LAY_shimmer);
    }

    public void updateUI(ClubMember member){
        EventDataManager.getInstance().loadNextEvent(member, onNextEventLoaded);
    }

    public void setEvent(Event sail){
        titleText.setText(sail.getName());
        dateText.setText(generateDateString(sail.getStartDateTime()));
        startTimeText.setText(generateTimeOfDayString(sail.getStartDateTime()));
        setBackgroundResource(sail.getType());

        showExtraInfo();

        // SET BACKGROUND IMAGE...
    }

    private void setBackgroundResource(EventType type){
        CommonUtils common = CommonUtils.getInstance();
        switch (type){
            case EVENING_EVENT:
                common.setImageResource(backgroundImage,R.drawable.img_evening_event);
                break;
            case GUIDED_SAIL:
                common.setImageResource(backgroundImage,R.drawable.img_guided_sail);
                break;
            case MEMBERS_SAIL:
                common.setImageResource(backgroundImage,R.drawable.img_members_sail);
                break;
        }

    }

    public void setNoEvent(){
        titleText.setText(resources.getText(R.string.label_no_next_event));
        hideExtraInfo();

        // SET BACKGROUND IMAGE...
    }

    private void hideExtraInfo(){
        startTimeText.setVisibility(View.GONE);
        dateText.setVisibility(View.GONE);
    }

    private void showExtraInfo(){
        startTimeText.setVisibility(View.VISIBLE);
        dateText.setVisibility(View.VISIBLE);
    }

    private String generateDateString(DateTime time){
        return time.toString("dd.MM.YYYY");
    }

    private String generateTimeOfDayString(DateTime time){
        return time.toString("HH:mm");

    }
}