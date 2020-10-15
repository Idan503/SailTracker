package com.idan_koren_israeli.sailtracker.activity;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.fragment.ProfileFragment;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnGalleryPhotoLoadListener;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnMemberLoadListener;
import com.idan_koren_israeli.sailtracker.club.GalleryPhoto;
import com.idan_koren_israeli.sailtracker.fragment.PhotoCollectionFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import java.util.InputMismatchException;

public class SearchActivity extends BaseActivity {

    private SearchView searchView;
    private ProfileFragment profileFragment;
    private PhotoCollectionFragment resultPhotos;
    private ClubMember resultMember;
    private TextView searchLabel;
    private String searchedPhone;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViews();
        setListeners();
        hideProfileFragment();
    }

    private void findViews(){
        searchView = findViewById(R.id.search_SEARCH_searchbar);
        searchLabel = findViewById(R.id.search_LBL_label);
        profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.search_FRAG_profile);
        resultPhotos = (PhotoCollectionFragment) getSupportFragmentManager().findFragmentById(R.id.search_FRAG_collection);
    }

    private void showProfileFragment(){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .show(profileFragment)
                .commit();
    }

    private void hideProfileFragment(){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .hide(profileFragment)
                .commit();
    }

    private void setListeners(){
        searchView.setOnQueryTextListener(onSearchPerformed);
    }

    //region Callbacks

    private SearchView.OnQueryTextListener onSearchPerformed = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String input) {
            try {
                searchedPhone = input;
                String formattedPhone = CommonUtils.getInstance().toPhoneString(searchedPhone);
                MemberDataManager.getInstance().loadMemberByPhone(formattedPhone, onMemberLoad);
            }
            catch ( InputMismatchException exception){
                Log.i(getClass().getSimpleName(), exception.toString());
                CommonUtils.getInstance().showToast("Phone number is invalid");
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

    private OnMemberLoadListener onMemberLoad = new OnMemberLoadListener() {
        @Override
        public void onMemberLoad(ClubMember memberLoaded) {
            resultMember = memberLoaded;
            if(resultMember==null&& searchedPhone !=null) {
                searchLabel.setText(getString(R.string.search_label_phone_not_found, searchedPhone));
                return;
            }
            if(memberLoaded!= MemberDataManager.getInstance().getCurrentMember()) {
                // Result found - loading the requested gallery
                MemberDataManager.getInstance().loadGallery(memberLoaded.getUid(), photoLoaded);
                profileFragment.setMember(memberLoaded);
                showProfileFragment();
                searchLabel.setVisibility(View.GONE);
            }
            else {
                CommonUtils.getInstance().showToast("You photos are in your own gallery");
                Intent intent = new Intent(SearchActivity.this, GalleryActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

    private OnGalleryPhotoLoadListener photoLoaded = new OnGalleryPhotoLoadListener() {
        @Override
        public void onPhotoLoaded(GalleryPhoto photo) {
            resultMember.addGalleryPhoto(photo);
            resultPhotos.setMember(resultMember);
            // a new photo is loaded, adding it to the internal stored member object and re-render ui
        }
    };

    //endregion
}