package com.idan_koren_israeli.sailtracker.home;

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
import com.idan_koren_israeli.sailtracker.common.ClubMember;
import com.idan_koren_israeli.sailtracker.common.BaseActivity;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.common.DatabaseManager;

import java.io.IOException;

public class HomeActivity extends BaseActivity implements OnLoginCompleteListener {

    private ClubMember user; // the current specific user's member object
    private LinearLayout loginLayout;
    private ProfileFragment profileFragment;
    private LoginFragment loginFragment;

    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbManager = DatabaseManager.getInstance();

        findViews();
        setListeners();
        loginFragment.setOnCompleteListener(this);

    }

    //region Init Functions

    private void findViews(){
        loginLayout = findViewById(R.id.home_LAY_login);
        profileFragment =(ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.home_FRAG_profile);
        loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.home_FRAG_login);

    }

    private void setListeners(){
        profileFragment.getProfileImage().setOnLongClickListener(changeProfilePhoto);
    }

    private void hideLoginFragment()
    {
        loginLayout.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().remove(loginFragment);
    }

    //endregion


    private void updateInterface(){
        profileFragment.setMember(user);
    }


    //region Profile Photo Change

    private View.OnLongClickListener changeProfilePhoto = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            if(user !=null) {
                CommonUtils.getInstance().dispatchChoosePictureIntent(HomeActivity.this);
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
                DatabaseManager.getInstance().storeProfilePhoto(photoBitmap, photoUploadSuccess, photoUploadFailure);
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


    //endregion

    @Override
    public void onLoginFinished(ClubMember authenticatedMember) {
        user = authenticatedMember;
        DatabaseManager.getInstance().setCurrentUser(user);
        hideLoginFragment();
        updateInterface();
    }

}