package com.idan_koren_israeli.sailtracker.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.AttributeSet;
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
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.activity.HistoryActivity;

/**
 * Profile card is a resizable fragment that is showing information about a user ( a club member)
 * With relevant data such as picture, name and number of points
 *
 */
public class ProfileFragment extends Fragment {

    private ImageView profilePhoto;
    private TextView nameText;
    private MaterialButton historyButton;
    private ClubMember member;
    private PointsStatusFragment pointsStatus;

    private CommonUtils common;

    private boolean showName, showPhoto, showPointsStatus, showHistoryButton;



    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        TypedArray type = context.obtainStyledAttributes(attrs,R.styleable.ProfileFragment);


        //Retrieve attributes from xml
        //By default, everything will be shown
        showName = type.getBoolean(R.styleable.ProfileFragment_show_name,true);
        showPhoto = type.getBoolean(R.styleable.ProfileFragment_show_photo,true);
        showPointsStatus = type.getBoolean(R.styleable.ProfileFragment_show_points_status,true);
        showHistoryButton = type.getBoolean(R.styleable.ProfileFragment_show_history_button,true);

        type.recycle();
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
        hideViews();
        setListeners();
        return parent;
    }

    private void findViews(View parent){
        profilePhoto = parent.findViewById(R.id.profile_IMG_picture);
        nameText = parent.findViewById(R.id.profile_LBL_name);
        historyButton = parent.findViewById(R.id.profile_BTN_my_sails);
        pointsStatus= (PointsStatusFragment) getChildFragmentManager().findFragmentById(R.id.profile_FRAG_points_status);
    }

    //Hides views based on attrs from xml (determent in onInflate)
    private void hideViews(){
        // By default, all views will be shown.

        if(!showName)
            hideName();

        if(!showPhoto)
            hidePhoto();

        if(!showPointsStatus)
            hidePointsStatus();

        if(!showHistoryButton)
            hideHistoryButton();

    }

    private void setListeners(){
        historyButton.setOnClickListener(new View.OnClickListener() {
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
            common.setImageResource(profilePhoto, uri);
        }
    };

    private OnFailureListener onProfileUriFailure = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            common.setImageResource(profilePhoto, R.drawable.ic_profile_default);
        }
    };


    //region Getters and Setters
    public ImageView getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(ImageView profilePhoto) {
        this.profilePhoto = profilePhoto;
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

    private void hideName(){
        nameText.setVisibility(View.GONE);

    }

    private void hidePhoto(){
        profilePhoto.setVisibility(View.GONE);
    }

    private void hidePointsStatus(){
        FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction()
                .hide(pointsStatus)
                .commit();
    }

    private void hideHistoryButton(){
        historyButton.setVisibility(View.GONE);
    }
}