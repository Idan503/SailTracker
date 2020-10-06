package com.idan_koren_israeli.sailtracker.gallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.common.BaseActivity;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.common.LoadingFragment;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;


/**
 *
 * This activity lets the user view a certain photo in a "full screen" mode.
 * And zoom in and translate via touch gestures
 *
 */

public class PhotoInspectActivity extends BaseActivity {

    private static final float MAX_SCALE = 5f;
    private static final float MIN_SCALE = 1f;
    private static final float SNAP_SCALE = 1.2f; // if scale is smaller, image snaps to center
    private static final int SNAP_TIME = 150; // in ms, snap to center

    private LoadingFragment loadingFragment;
    private ImageView imageView;
    private GalleryPhoto displayedPhoto;

    private FloatingActionButton deleteFAB, backFAB;

    private float scaleFactor = 1.0f;
    //private ScaleGestureDetector scaleDetector;
    private float imageX, imageY;

    private ClubMember currentMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_inspect);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        // Letting the user change to landscape mode when viewing photo
        findViews();
        loadingFragment.show();

        displayedPhoto = (GalleryPhoto) getIntent().getSerializableExtra(PhotoCollectionFragment.KEYS.PHOTO_OBJ);
        loadPhoto();
        setListeners();
        currentMember = MemberDataManager.getInstance().getCurrentUser();

        // Setting the scale zoom gestures
        //scaleDetector = new ScaleGestureDetector(this, new ScaleListener());

    }

    private void findViews(){
        imageView = findViewById(R.id.photo_inspect_IMG_photo);
        loadingFragment = (LoadingFragment) getSupportFragmentManager().findFragmentById(R.id.photo_inspect_FRAG_loading);
        deleteFAB = findViewById(R.id.photo_inspect_FAB_delete);
        backFAB = findViewById(R.id.photo_inspect_FAB_back);
    }

    private void setListeners(){
        deleteFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemberDataManager.getInstance().deleteGalleryPhoto(currentMember.getUid(),displayedPhoto,photoDeleteSuccess, photoDeleteFail);
                currentMember.removeGalleryPhoto(displayedPhoto);
                MemberDataManager.getInstance().storeMember(currentMember);
                finish();
            }
        });

        backFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }



    private void loadPhoto(){
        CommonUtils.getInstance().setImageResource(imageView, Uri.parse(displayedPhoto.getUrl()),imageLoadListener);
    }


    //region Image Scale Gestures

    //  redirects all touch events in the activity to the gesture detector
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                imageX = imageView.getX() - event.getRawX();
                imageY = imageView.getY() - event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                imageView.animate().x(event.getRawX() + imageX).y(event.getRawY() + imageY).setDuration(0).start();
                break;
            case MotionEvent.ACTION_UP:
                if(scaleFactor < SNAP_SCALE)
                    imageView.animate().x(0).y(0).setDuration(SNAP_TIME).start();
                scaleFactor = 1f;
                break;
            default:
                imageView.animate().x(0).y(0).setDuration(SNAP_TIME).start();
                scaleFactor = 1f;
                break;
        }
        return true;
    }

    //region Photo Listeners

    private RequestListener<Drawable> imageLoadListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            loadingFragment.hide();
            return false;
        }
    };

    private OnSuccessListener<Void> photoDeleteSuccess = new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            CommonUtils.getInstance().showToast("Photo deleted successfully");
            finish();
        }
    };

    private OnFailureListener photoDeleteFail = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            CommonUtils.getInstance().showToast("Please try again later");

        }
    };

    //endregion
}