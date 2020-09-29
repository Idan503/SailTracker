package com.idan_koren_israeli.sailtracker.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.UploadTask;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.common.BaseActivity;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.firebase.MembersDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnGalleryPhotoLoadListener;
import com.idan_koren_israeli.sailtracker.home.ProfileFragment;

public class GalleryActivity extends BaseActivity {

    private FloatingActionButton addPhotoButton;
    private ProfileFragment profileFrag;
    private PhotoCollectionFragment photosFrag;
    private ClubMember member;
    private MembersDataManager dbManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        dbManager = MembersDataManager.getInstance();
        member = dbManager.getCurrentUser();

        findViews();
        setListeners();

        dbManager.loadGallery(dbManager.getCurrentUser().getUid(),photoLoaded);
        profileFrag.setMember(dbManager.getCurrentUser());
        photosFrag.setMember(dbManager.getCurrentUser());
    }

    private OnGalleryPhotoLoadListener photoLoaded = new OnGalleryPhotoLoadListener() {
        @Override
        public void onPhotoLoaded(GalleryPhoto photo) {
            Log.i("pttt", " Photo loaded : " + photo.getUri().toString());
            member.addGalleryPhoto(photo);
            photosFrag.setMember(member);
            // a new photo is loaded, re-rendering ui
        }
    };


    private void findViews(){
        addPhotoButton = findViewById(R.id.gallery_FAB_add_photo);
        profileFrag = (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.gallery_FRAG_profile);
        photosFrag = (PhotoCollectionFragment) getSupportFragmentManager().findFragmentById(R.id.gallery_FRAG_photo_collection);

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
            recreate(); // restart activity to load new photo
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