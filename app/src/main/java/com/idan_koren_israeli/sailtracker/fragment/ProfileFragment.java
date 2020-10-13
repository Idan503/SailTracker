package com.idan_koren_israeli.sailtracker.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
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
    private EditText newNameEditText; //will be used only when user changes name



    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onInflate(@NonNull Context context,@NonNull AttributeSet attrs, Bundle savedInstanceState) {
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
        // Photo can only be changed from home scree, so listener is not in the fragment.

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                intent.putExtra(HistoryActivity.KEYS.MEMBER_TO_SHOW,member);
                startActivity(intent);
                // Caller activity is purposefully not finished.
                // "My sails" activity would be finished and user will get back to the caller activity
                }
        });

        nameText.setOnClickListener(onNameClick);
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
        if(showHistoryButton){
            int numOfEvents = member.getEventCount();
            String historyButtonLabel;
            if(numOfEvents==1){
                historyButtonLabel = String.format(getResources().getString(R.string.profile_events_history_single_label), numOfEvents);
            }
            else
                historyButtonLabel = String.format(getResources().getString(R.string.profile_events_history_label), numOfEvents);
            historyButton.setText(historyButtonLabel);
        }
    }


    //region Profile Photo Load Listeners
    private OnSuccessListener<Uri> onProfileUriSuccess = new OnSuccessListener<Uri>(){
        @Override
        public void onSuccess(Uri uri) {
            common.setImageResource(profilePhoto, uri);
        }
    };

    private OnFailureListener onProfileUriFailure = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            common.setImageResource(profilePhoto, R.drawable.img_blank_profile);
            // 404 error, user not set a photo, showing default blank profile photo
        }
    };

    //endregion


    //region Rename Profile Member Listener
    private View.OnClickListener onNameClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            newNameEditText = new EditText(view.getContext());
            newNameEditText.setHint(view.getContext().getText(R.string.label_choose_new_name));
            if(member!=null)
                newNameEditText.setText(member.getName());

            new MaterialAlertDialogBuilder(view.getContext())
                    .setTitle("Change Display Name")
                    .setView(newNameEditText)
                    .setPositiveButton("Ok", onNewNameSet)
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    };


    private DialogInterface.OnClickListener onNewNameSet = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if(newNameEditText==null)
                return;

            String newName = newNameEditText.getText().toString();
            String oldName = member.getName();
            if(!newName.matches("") && !newName.equals(oldName)) {
                member.setName(newName);
                MemberDataManager.getInstance().storeMember(member);
            }
            else{
                CommonUtils.getInstance().showToast("Please insert a new name");
            }
        }
    };

    //endregion

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