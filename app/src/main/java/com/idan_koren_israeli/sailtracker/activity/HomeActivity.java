package com.idan_koren_israeli.sailtracker.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import com.idan_koren_israeli.sailtracker.fragment.LoadingFragment;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnLoginFinishedListener;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.fragment.NavigationBarFragment;
import com.idan_koren_israeli.sailtracker.fragment.ProfileFragment;
import com.idan_koren_israeli.sailtracker.fragment.LoginFragment;
import com.idan_koren_israeli.sailtracker.fragment.NextEventFragment;

import java.io.IOException;

public class HomeActivity extends BaseActivity {

    private ClubMember user; // the current specific user's member object
    private LinearLayout loginLayout;
    private ImageView bannerImage;
    private ProfileFragment profileFragment;
    private LoginFragment loginFragment;
    private LoadingFragment loadingFragment;
    private NavigationBarFragment navigationBarFragment;
    private NextEventFragment nextEventFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        findViews();
        setListeners();

        loadBannerImage();

        user = MemberDataManager.getInstance().getCurrentUser();
        if(user!=null) {
            // No need for login, user is already authenticated
            hideLoginFragment();
            updateInterface();
            navigationBarFragment.setClickable(true);

        }
        else{
            // User is not logged in, activity will wait for LoginFragment to finish
            loginFragment.setOnCompleteListener(onLoginFinished);
            navigationBarFragment.setClickable(false);
        }

    }

    private void loadBannerImage() {
        CommonUtils.getInstance().setImageResource(bannerImage, R.drawable.img_app_banner);
    }


    //region Init Functions

    private void findViews(){
        loginLayout = findViewById(R.id.home_LAY_login);
        bannerImage = findViewById(R.id.home_IMG_banner);
        profileFragment =(ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.home_FRAG_profile);
        loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.home_FRAG_login);
        loadingFragment = (LoadingFragment) getSupportFragmentManager().findFragmentById(R.id.home_FRAG_loading);
        nextEventFragment = (NextEventFragment) getSupportFragmentManager().findFragmentById(R.id.home_FRAG_next_sailing);
        navigationBarFragment = (NavigationBarFragment) getSupportFragmentManager().findFragmentById(R.id.home_FRAG_navigator_bar);
    }


    private void setListeners(){
        profileFragment.getProfilePhoto().setOnClickListener(changeProfilePhoto);
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

    private View.OnClickListener changeProfilePhoto = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(user !=null) {
                CommonUtils common = CommonUtils.getInstance();
                common.dispatchChoosePictureIntent(HomeActivity.this,"Change profile picture...");
                updateInterface();
            }
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
                MemberDataManager.getInstance().setCurrentMember(user);
                hideLoginFragment();
                updateInterface();
                navigationBarFragment.setClickable(true);
            }
        }
    };


}