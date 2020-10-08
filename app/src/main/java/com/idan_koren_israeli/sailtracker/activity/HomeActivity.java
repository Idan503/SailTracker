package com.idan_koren_israeli.sailtracker.activity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.UploadTask;
import com.idan_koren_israeli.sailtracker.firebase.LoginManager;
import com.idan_koren_israeli.sailtracker.fragment.LoadingFragment;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnLoginFinishedListener;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.fragment.ProfileFragment;
import com.idan_koren_israeli.sailtracker.fragment.LoginFragment;
import com.idan_koren_israeli.sailtracker.fragment.NextEventFragment;

import java.io.IOException;

public class HomeActivity extends BaseActivity {

    private ClubMember user; // the current specific user's member object
    private LinearLayout loginLayout;
    private ProfileFragment profileFragment;
    private LoginFragment loginFragment;
    private LoadingFragment loadingFragment;
    private NextEventFragment nextEventFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        findViews();
        setListeners();

        user = MemberDataManager.getInstance().getCurrentUser();
        if(user!=null) {
            // No need for login, user is already authenticated
            hideLoginFragment();
            updateInterface();
        }
        else
            loginFragment.setOnCompleteListener(onLoginFinished);
    }


    //region Init Functions

    private void findViews(){
        loginLayout = findViewById(R.id.home_LAY_login);
        profileFragment =(ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.home_FRAG_profile);
        loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.home_FRAG_login);
        loadingFragment = (LoadingFragment) getSupportFragmentManager().findFragmentById(R.id.home_FRAG_loading);
        nextEventFragment = (NextEventFragment) getSupportFragmentManager().findFragmentById(R.id.home_FRAG_next_sailing);

    }


    private void setListeners(){
        profileFragment.getProfilePhoto().setOnLongClickListener(changeProfilePhoto);
    }

    private void hideLoginFragment()
    {
        if(loginLayout!=null) {
            loginLayout.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().remove(loginFragment);
        }
    }

    //endregion


    private void updateInterface(){
        if(profileFragment!=null) {
            profileFragment.setMember(user);
            nextEventFragment.updateUI(user);

        }
    }


    //region Profile Photo Change

    private View.OnLongClickListener changeProfilePhoto = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            if(user !=null) {
                CommonUtils.getInstance().dispatchChoosePictureIntent(HomeActivity.this);
                updateInterface();
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Code gets to here after camera intent is finished, data is the users chosen photo
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
                MemberDataManager.getInstance().storeProfilePhoto(photoBitmap, photoUploadSuccess, photoUploadFailure);
                loadingFragment.setMessage("Uploading Profile Picture...");
                loadingFragment.show();
            }
        }

    }

    private OnSuccessListener<UploadTask.TaskSnapshot> photoUploadSuccess = new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            CommonUtils.getInstance().showToast("Profile photo saved!");
            profileFragment.updateUI(); // Updating interface to show the updated photo
            loadingFragment.hide();
        }
    };

    private OnFailureListener photoUploadFailure = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            CommonUtils.getInstance().showToast("Problem occurred.");
        }
    };


    //endregion

    private OnLoginFinishedListener onLoginFinished = new OnLoginFinishedListener() {
        @Override
        public void onLoginFinished(ClubMember authenticatedMember) {
            user = authenticatedMember;
            if(user!=null) {
                MemberDataManager.getInstance().setCurrentUser(user);
                hideLoginFragment();
                updateInterface();
            }
        }
    };


}