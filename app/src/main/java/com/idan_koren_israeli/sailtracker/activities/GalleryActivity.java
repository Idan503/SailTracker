package com.idan_koren_israeli.sailtracker.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.UploadTask;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.common.DatabaseManager;
import com.idan_koren_israeli.sailtracker.fragments.ProfileFragment;

public class GalleryActivity extends BaseActivity {

    FloatingActionButton addPhotoButton;
    ProfileFragment profileFrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        findViews();
        setListeners();
    }


    private void findViews(){
        addPhotoButton = findViewById(R.id.gallery_FAB_add_photo);
        profileFrag = (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.gallery_FRAG_profile);

    }

    private void setListeners(){
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.getInstance().dispatchTakePictureIntent(GalleryActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if(extras!=null) {
                Bitmap photoBitmap = (Bitmap) extras.get("data");
                if(photoBitmap!=null)
                    DatabaseManager.getInstance().uploadGalleryPhoto(photoBitmap, photoUploadSuccess, photoUploadFailure);
            }
        }
    }

    private OnSuccessListener<UploadTask.TaskSnapshot> photoUploadSuccess = new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            CommonUtils.getInstance().showToast("Gallery image saved!");
        }
    };

    private OnFailureListener photoUploadFailure = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            CommonUtils.getInstance().showToast("Problem occurred.");
        }
    };
}