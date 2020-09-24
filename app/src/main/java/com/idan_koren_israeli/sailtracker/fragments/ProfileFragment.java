package com.idan_koren_israeli.sailtracker.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idan_koren_israeli.sailtracker.ClubMember;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.activities.BaseActivity;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;

import java.util.Locale;
import java.util.Objects;

/**
 * Profile card is a resizable fragment that is showing information about a user ( a club member)
 * With relevant data such as picture, name and number of points
 *
 */
public class ProfileFragment extends Fragment {

    ImageView profileImage, pointsImage;
    TextView nameText, numOfPointsText, numOfSailsText;
    ClubMember member;



    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View parent = inflater.inflate(R.layout.fragment_profile_card, container, false);
        // Inflate the layout for this fragment
        findViews(parent);
        return parent;
    }

    private void findViews(View parent){
        profileImage = parent.findViewById(R.id.profile_IMG_picture);
        pointsImage = parent.findViewById(R.id.profile_IMG_points);
        nameText = parent.findViewById(R.id.profile_LBL_name);
        numOfPointsText = parent.findViewById(R.id.profile_LBL_points_count);
        numOfSailsText = parent.findViewById(R.id.profile_LBL_sails_count);
    }

    public void setMember(ClubMember member){
        this.member = member;
        updateDisplayData(member);
    }

    private void updateDisplayData(ClubMember member){
        nameText.setText(member.getName());
        if(member.getProfilePictureUrl()!=null)
            CommonUtils.getInstance().setImageResource(profileImage, Uri.parse(member.getProfilePictureUrl()));
        else
            CommonUtils.getInstance().setImageResource(profileImage,R.drawable.ic_profile_default);
        numOfPointsText.setText(String.format(Locale.US,"%d", member.getPointsCount()));
        numOfSailsText.setText(String.format(Locale.US,"%d", member.getSailsCount()));
    }

    /*
    private void updatePicture(){
        if(getActivity()==null)
            return; // onAttach is not called yet.
        CommonUtils.getInstance().dispatchChoosePictureIntent(((BaseActivity) Objects.requireNonNull(getActivity()));



    }

     */




}