package com.idan_koren_israeli.sailtracker.gallery;

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
import com.idan_koren_israeli.sailtracker.common.BaseActivity;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.common.DatabaseManager;
import com.idan_koren_israeli.sailtracker.home.ProfileFragment;

public class GalleryActivity extends BaseActivity {

    FloatingActionButton addPhotoButton;
    ProfileFragment profileFrag;

    private DatabaseManager dbManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        dbManager = DatabaseManager.getInstance();

        findViews();
        setListeners();

        dbManager.loadGallery();
        profileFrag.setMember(dbManager.getCurrentUser());
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

    //region Picture Taken Handling

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Code gets to here after user comes back from camera intent, and took a picture
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if(extras!=null) {
                Bitmap photoBitmap = (Bitmap) extras.get("data");
                if(photoBitmap!=null)
                    dbManager.storeGalleryPhoto(photoBitmap, photoUploadSuccess, photoUploadFailure);
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

    //endregion
}