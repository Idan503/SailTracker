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
import android.widget.SearchView;

public class SearchActivity extends BaseActivity {

    private SearchView searchView;
    private ProfileFragment profile;
    private PhotoCollectionFragment resultPhotos;
    private ClubMember resultMember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViews();
        setListeners();
    }

    private void findViews(){
        searchView = findViewById(R.id.search_SEARCH_searchbar);
        profile = (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.search_FRAG_profile);
        resultPhotos = (PhotoCollectionFragment) getSupportFragmentManager().findFragmentById(R.id.search_FRAG_collection);

    }

    private void setListeners(){
        searchView.setOnQueryTextListener(onSearchPerformed);
    }

    private SearchView.OnQueryTextListener onSearchPerformed = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String input) {
            String phoneNumber = CommonUtils.getInstance().toPhoneString(input);
            MemberDataManager.getInstance().loadMemberByPhone(phoneNumber, onMemberLoad);
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
            if(resultMember==null) {
                CommonUtils.getInstance().showToast("User could not be found");
                return;
            }
            if(memberLoaded!= MemberDataManager.getInstance().getCurrentUser()) {
                MemberDataManager.getInstance().loadGallery(memberLoaded.getUid(), photoLoaded);
                profile.setMember(memberLoaded);
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
}