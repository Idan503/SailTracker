package com.idan_koren_israeli.sailtracker.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.idan_koren_israeli.sailtracker.common.ClubMember;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.common.DatabaseManager;
import com.idan_koren_israeli.sailtracker.home.ProfileFragment;

public class GalleryActivity extends BaseActivity {

    private FloatingActionButton addPhotoButton;
    private ProfileFragment profileFrag;
    private PhotoCollectionFragment photosFrag;
    private ClubMember user;
    private DatabaseManager dbManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        dbManager = DatabaseManager.getInstance();
        user = dbManager.getCurrentUser();

        findViews();
        setListeners();

        dbManager.loadGallery(onPhotoLoadedListener);
        profileFrag.setMember(dbManager.getCurrentUser());
        photosFrag.setMember(dbManager.getCurrentUser());
    }

    private OnSuccessListener<Uri> onPhotoLoadedListener = new OnSuccessListener<Uri>() {
        @Override
        public void onSuccess(Uri uri) {
            GalleryPhoto newPhoto = new GalleryPhoto(uri, user.getGalleryPhotos().size());
            if(!user.getGalleryPhotos().contains(newPhoto))
                user.addGalleryPhoto(newPhoto);
            Log.i("pttt", "Loaded " + uri);
            photosFrag.setMember(user);
            // Each time photo is loaded, we will re-assign the member to the recycleview
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