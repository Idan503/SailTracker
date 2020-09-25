package com.idan_koren_israeli.sailtracker.home;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.idan_koren_israeli.sailtracker.common.ClubMember;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.common.DatabaseManager;

import java.util.Locale;

/**
 * Profile card is a resizable fragment that is showing information about a user ( a club member)
 * With relevant data such as picture, name and number of points
 *
 */
public class ProfileFragment extends Fragment {

    private ImageView profileImage, pointsImage;
    private TextView nameText, numOfPointsText, numOfSailsText;
    private ClubMember member;

    private CommonUtils common;



    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        common = CommonUtils.getInstance();
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
        updateUI();
    }

    public void updateUI(){
        if(member==null)
            return; // No member associated
        nameText.setText(member.getName());
        DatabaseManager.getInstance().loadProfilePhoto(member.getUid(), onProfileUriSuccess, onProfileUriFailure);
        numOfPointsText.setText(String.format(Locale.US,"%d", member.getPointsCount()));
        numOfSailsText.setText(String.format(Locale.US,"%d", member.getSailsCount()));
    }


    private OnSuccessListener<Uri> onProfileUriSuccess = new OnSuccessListener<Uri>(){
        @Override
        public void onSuccess(Uri uri) {
            common.setImageResource(profileImage, uri);
        }
    };

    private OnFailureListener onProfileUriFailure = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            common.setImageResource(profileImage, R.drawable.ic_profile_default);
            Log.i("pttt", "404e");
        }
    };

    public ImageView getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ImageView profileImage) {
        this.profileImage = profileImage;
    }

    public ImageView getPointsImage() {
        return pointsImage;
    }

    public void setPointsImage(ImageView pointsImage) {
        this.pointsImage = pointsImage;
    }

    public TextView getNameText() {
        return nameText;
    }

    public void setNameText(TextView nameText) {
        this.nameText = nameText;
    }

    public TextView getNumOfPointsText() {
        return numOfPointsText;
    }

    public void setNumOfPointsText(TextView numOfPointsText) {
        this.numOfPointsText = numOfPointsText;
    }

    public TextView getNumOfSailsText() {
        return numOfSailsText;
    }

    public void setNumOfSailsText(TextView numOfSailsText) {
        this.numOfSailsText = numOfSailsText;
    }

    public ClubMember getMember() {
        return member;
    }
}