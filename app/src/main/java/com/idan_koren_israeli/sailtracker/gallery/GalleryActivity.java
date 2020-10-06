package com.idan_koren_israeli.sailtracker.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.UploadTask;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.common.BaseActivity;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.common.LoadingFragment;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnGalleryPhotoLoadListener;
import com.idan_koren_israeli.sailtracker.common.ProfileFragment;

import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;

public class GalleryActivity extends BaseActivity {

    private FloatingActionButton addPhotoButton;
    private ProfileFragment profileFrag;
    private PhotoCollectionFragment photosFrag;
    private ClubMember member;
    private MemberDataManager dbManager;

    private String capturedPhotoPath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        dbManager = MemberDataManager.getInstance();
        member = dbManager.getCurrentUser();

        findViews();
        setListeners();

        photosFrag.setMember(dbManager.getCurrentUser());
        dbManager.loadGallery(dbManager.getCurrentUser().getUid(),photoLoaded);

        profileFrag.setMember(dbManager.getCurrentUser());
    }

    private OnGalleryPhotoLoadListener photoLoaded = new OnGalleryPhotoLoadListener() {
        @Override
        public void onPhotoLoaded(GalleryPhoto photo) {
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
                //CommonUtils.getInstance().dispatchTakePictureIntent(GalleryActivity.this);
                dispatchTakePictureIntent();
            }
        });
    }

    //region Picture Capture & Upload

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.idan_koren_israeli.sailtracker.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = Long.toString(DateTime.now().getMillis());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        capturedPhotoPath = image.getAbsolutePath();
        return image;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Code gets to here after user comes back from camera intent, and took a picture
        if (requestCode == 1 && resultCode == RESULT_OK) {
            File image = new File(capturedPhotoPath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
            Bitmap rotatedBitmap = bitmap;
            try{
                rotatedBitmap = rotatePhotoBitmap(capturedPhotoPath,bitmap);
            }catch (IOException exception){
                CommonUtils.getInstance().showToast("Problem occurred, could not rotate captured picture.");
            }
            dbManager.storeGalleryPhoto(rotatedBitmap, photoUploadSuccess, photoUploadFailure);
            photosFrag.showLoading();
        }

    }

    // By default in some phones photos are only taken in landscape mode.
    // These functions will rotate it.
    //https://stackoverflow.com/questions/14066038/why-does-an-image-captured-using-camera-intent-gets-rotated-on-some-devices-on-a
    private Bitmap rotatePhotoBitmap(String filePath, Bitmap bitmap) throws IOException {
        ExifInterface ei = new ExifInterface(filePath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }
        return rotatedBitmap;
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private OnSuccessListener<UploadTask.TaskSnapshot> photoUploadSuccess = new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            CommonUtils.getInstance().showToast("Gallery image saved!");
            photosFrag.hideLoading();
            recreate();
        }
    };

    private OnFailureListener photoUploadFailure = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            CommonUtils.getInstance().showToast("Problem occurred.");
            photosFrag.hideLoading();
        }
    };





    //endregion
}