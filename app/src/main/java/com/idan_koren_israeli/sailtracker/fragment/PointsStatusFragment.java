package com.idan_koren_israeli.sailtracker.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;


public class PointsStatusFragment extends Fragment {

    private static final int pointsToAdd = 10;

    private MaterialButton addButton;
    private TextView pointsCountText;

    public PointsStatusFragment() {
        // Required empty public constructor
    }

    public static PointsStatusFragment newInstance(String param1, String param2) {
        PointsStatusFragment fragment = new PointsStatusFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent =  inflater.inflate(R.layout.fragment_points_status, container, false);
        findViews(parent);

        // by default view will start as the current member
        ClubMember current = MemberDataManager.getInstance().getCurrentUser();
        if(current!=null)
            setMember(current);
        return parent;
    }

    public void setMember(ClubMember member){
        setAddListener(member);
        updateCount(member.getPointsCount());
    }

    public void updateCount(int count){
        this.pointsCountText.setText(String.valueOf(count));
    }

    private void findViews(View parent){
        pointsCountText = parent.findViewById(R.id.points_status_LBL_points_count);
        addButton = parent.findViewById(R.id.points_status_BTN_add);
    }

    private void setAddListener(final ClubMember member){
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                member.addPoints(pointsToAdd);
                MemberDataManager.getInstance().storeMember(member);
                updateCount(member.getPointsCount());
            }
        });
    }


}