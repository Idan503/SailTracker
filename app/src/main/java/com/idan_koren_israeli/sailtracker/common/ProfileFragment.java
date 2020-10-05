package com.idan_koren_israeli.sailtracker.common;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.history.HistoryActivity;

/**
 * Profile card is a resizable fragment that is showing information about a user ( a club member)
 * With relevant data such as picture, name and number of points
 *
 */
public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView nameText;
    private MaterialButton mySails;
    private ClubMember member;
    private PointsStatusFragment pointsStatus;

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
        setListeners();
        return parent;
    }

    private void findViews(View parent){
        profileImage = parent.findViewById(R.id.profile_IMG_picture);
        nameText = parent.findViewById(R.id.profile_LBL_name);
        mySails = parent.findViewById(R.id.profile_BTN_my_sails);
        pointsStatus= (PointsStatusFragment) getChildFragmentManager().findFragmentById(R.id.profile_FRAG_points_status);
    }

    private void setListeners(){
        mySails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(intent);
                // Caller activity is purposefully not finished.
                // "My sails" activity would be finished and user will get back to the caller activity
                }
        });
    }


    public void setMember(ClubMember member){
        this.member = member;
        updateUI();
    }

    public void updateUI(){
        if(member==null)
            return; // No member associated
        nameText.setText(member.getName());
        MemberDataManager.getInstance().loadProfilePhoto(member.getUid(), onProfileUriSuccess, onProfileUriFailure);
        pointsStatus.setMember(member);
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
        }
    };


    //region Getters and Setters
    public ImageView getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ImageView profileImage) {
        this.profileImage = profileImage;
    }

    public TextView getNameText() {
        return nameText;
    }

    public void setNameText(TextView nameText) {
        this.nameText = nameText;
    }

    public ClubMember getMember() {
        return member;
    }

    public PointsStatusFragment getPointsStatus() {
        return pointsStatus;
    }

    public void setPointsStatus(PointsStatusFragment pointsStatus) {
        this.pointsStatus = pointsStatus;
    }

    //endregion
}