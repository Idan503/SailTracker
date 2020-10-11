package com.idan_koren_israeli.sailtracker.activity;

import androidx.annotation.NonNull;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.fragment.LoadingFragment;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.club.GalleryPhoto;

import org.joda.time.Chronology;
import org.joda.time.DateTime;


/**
 *
 * This activity lets the user view a certain photo in a "full screen" mode.
 * And zoom in and translate via touch gestures
 *
 */

public class PhotoInspectActivity extends BaseActivity {

    public interface KEYS{
        String MEMBER_TOOK = "member_took";
        String PHOTO_OBJ = "photo_obj";
    }


    private LoadingFragment loadingFragment;
    private WebView imageWeb;
    private GalleryPhoto displayedPhoto;
    private TextView imageInfoText;

    private FloatingActionButton deleteFAB, backFAB;

    private ClubMember photoMember; // Clubmember who took the photo

    private static final int FADE_IN_DURATION = 450;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_inspect);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        // Letting the user change to landscape mode when viewing photo

        findViews();
        retrieveIntentPrefs();

        setListeners();
        initPhotoView();

    }


    private void findViews(){
        loadingFragment = (LoadingFragment) getSupportFragmentManager().findFragmentById(R.id.photo_inspect_FRAG_loading);
        imageWeb = findViewById(R.id.photo_inspect_IMG_photo);
        deleteFAB = findViewById(R.id.photo_inspect_FAB_delete);
        backFAB = findViewById(R.id.photo_inspect_FAB_back);
        imageInfoText = findViewById(R.id.photo_inspect_LBL_info);
    }

    private void setListeners(){
        deleteFAB.setOnClickListener(onDeleteButtonClicked);

        backFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    // When current user is viewing his own photo, it can be deleted
    private void retrieveIntentPrefs(){
        deleteFAB.setVisibility(View.GONE); // Starts at hidden
        photoMember = (ClubMember) getIntent().getSerializableExtra(KEYS.MEMBER_TOOK);
        displayedPhoto = (GalleryPhoto) getIntent().getSerializableExtra(KEYS.PHOTO_OBJ);

    }

    private void initPhotoView(){
        // Using a web-image to control pinch to zoom and move by touch
        imageWeb.getSettings().setBuiltInZoomControls(true);
        imageWeb.getSettings().setDisplayZoomControls(false);
        imageWeb.getSettings().setLoadWithOverviewMode(true);
        imageWeb.getSettings().setUseWideViewPort(true);
        imageWeb.getSettings().setSupportZoom(true);
        imageWeb.loadUrl(displayedPhoto.getUrl());
        imageWeb.setAlpha(0); // Loading process doesnt look good, will fade in at the end 
        loadingFragment.show(); // Will be hidden when picture finishes downloading

        imageWeb.setWebViewClient(finishedLoadingListener);

    }




    //region Loading Callbacks
    private WebViewClient finishedLoadingListener = new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            loadingFragment.hide();
            deleteFAB.setVisibility(View.VISIBLE);
            setDisplayedPhotoInfo();


            // Smooth showing by fade in simple animation
            imageWeb.animate()
                    .alphaBy(1)
                    .setDuration(FADE_IN_DURATION)
                    .start();

            // Photos can be deleted via inspect activity iff member owns this collection (current user)
            if(photoMember == MemberDataManager.getInstance().getCurrentMember()){
                deleteFAB.setAlpha(0f);
                deleteFAB.animate()
                        .alphaBy(1)
                        .setDuration(FADE_IN_DURATION)
                        .start();
            }

        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
        }
    };



    //endregion

    private void setDisplayedPhotoInfo(){
        int createdTimestamp = (int) displayedPhoto.getTimeCreated();



        String label = "";
        label += "Was taken by " + photoMember.getName();
        label += " on " + (new DateTime(0)).plusSeconds(createdTimestamp).toString("dd.MM.YYYY");

        imageInfoText.setText(label);
    }



    //region Photo Delete Callbacks

    private View.OnClickListener onDeleteButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(view.getContext());
            dialog.setMessage("Are you sure you want to delete this photo?");
            dialog.setTitle("Delete Image");
            dialog.setPositiveButton("Delete", onDeleteConfirmed);
            dialog.setNegativeButton("Cancel", null);
            dialog.show();
        }
    };

    private DialogInterface.OnClickListener onDeleteConfirmed = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if(photoMember!=MemberDataManager.getInstance().getCurrentMember())
                return; //For extra safety - a given member can only delete his own photos
            MemberDataManager.getInstance().deleteGalleryPhoto(photoMember.getUid(),displayedPhoto,photoDeleteSuccess, photoDeleteFail);
            photoMember.removeGalleryPhoto(displayedPhoto);
            MemberDataManager.getInstance().storeMember(photoMember);
            finish();
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