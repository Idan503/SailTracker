package com.idan_koren_israeli.sailtracker.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import com.idan_koren_israeli.sailtracker.ClubMember;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.fragments.OnLoginCompleteListener;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.common.UserDataManager;
import com.idan_koren_israeli.sailtracker.fragments.LoginFragment;
import com.idan_koren_israeli.sailtracker.fragments.ProfileFragment;

import java.io.IOException;

public class HomeActivity extends BaseActivity {

    private ClubMember member; // the current specific user's member object
    private LinearLayout loginLayout;
    private ProfileFragment profileFragment;
    private LoginFragment loginFragment;

    private UserDataManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbManager = UserDataManager.getInstance();

        findViews();
        setListeners();
        loginFragment.setOnCompleteListener(loginCompleteListener);


        if(loginFragment.isLoggedIn()){
            hideLoginFragment(); // User is already logged-in
            member = dbManager.getCurrentMember();
            if(member!=null)
                updateInterface();
        }

    }

    private void findViews(){
        loginLayout = findViewById(R.id.home_LAY_login);
        profileFragment =(ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.home_FRAG_profile);
        loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.home_FRAG_login);

    }

    private void setListeners(){
        profileFragment.getProfileImage().setOnLongClickListener(updateProfilePhoto);
    }



    private void hideLoginFragment(){
        loginLayout.setVisibility(View.GONE);
    }


    private View.OnLongClickListener updateProfilePhoto = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            if(member!=null) {
                CommonUtils.getInstance().dispatchChoosePictureIntent(HomeActivity.this);
                return true;
            }
            return false;
        }
    };

    private OnLoginCompleteListener loginCompleteListener = new OnLoginCompleteListener() {
        @Override
        public void onLoginFinished(ClubMember authenticatedMember) {
            member = authenticatedMember;
            hideLoginFragment();
            updateInterface();
        }
    };

    // Making the data appears on screen to the current user's data
    private void updateInterface(){
        profileFragment.setMember(member);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Updating the profile photo of the member
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap photoBitmap = null;
            if(data.getData()!=null) {
                // Photo is from device storage
                Uri imageUri = data.getData();
                try {
                    photoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                // Photo is from camera
                Bundle extras = data.getExtras();
                if (extras != null) {
                    photoBitmap = (Bitmap) extras.get("data");
                }
            }
            if (photoBitmap != null) {
                UserDataManager.getInstance().uploadProfileImage(photoBitmap, photoUploadSuccess, photoUploadFailure);
            }
        }

    }

    private OnSuccessListener<UploadTask.TaskSnapshot> photoUploadSuccess = new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            CommonUtils.getInstance().showToast("Profile photo saved!");
            profileFragment.updateUI(); // Updating interface to show the updated photo
        }
    };

    private OnFailureListener photoUploadFailure = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            CommonUtils.getInstance().showToast("Problem occurred.");
        }
    };


}