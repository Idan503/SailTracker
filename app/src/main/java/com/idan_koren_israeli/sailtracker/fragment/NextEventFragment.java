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
import com.idan_koren_israeli.sailtracker.firebase.EventDataManager;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
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
        View parent = inflater.inflate(R.layout.fragment_next_sail_card, container, false);
        resources = parent.getResources();
        findViews(parent);

        return parent;
    }

    private OnEventLoadedListener onNextSailLoaded = new OnEventLoadedListener() {
        @Override
        public void onEventLoaded(Event sail) {
            if(sail!=null && sail.getStartDateTime().getMillis() > DateTime.now().getMillis())
                setSail(sail);
            else
                setNoSail();
        }
    };

    private void findViews(View parent){
        titleText = parent.findViewById(R.id.next_sail_LBL_sail_name);
        backgroundImage = parent.findViewById(R.id.next_sail_IMG_background);
        startTimeText = parent.findViewById(R.id.next_sail_LBL_start_time);
        dateText = parent.findViewById(R.id.next_sail_LBL_date);
    }

    public void updateUI(){
        ClubMember currentMember = MemberDataManager.getInstance().getCurrentUser();
        EventDataManager.getInstance().loadNextEvent(currentMember, onNextSailLoaded);
    }

    public void setSail(Event sail){
        titleText.setText(sail.getName());
        dateText.setText(generateDateString(sail.getStartDateTime()));
        startTimeText.setText(generateTimeOfDayString(sail.getStartDateTime()));
        showExtraInfo();

        // SET BACKGROUND IMAGE...
    }

    public void setNoSail(){
        titleText.setText(resources.getText(R.string.no_next_sail));
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