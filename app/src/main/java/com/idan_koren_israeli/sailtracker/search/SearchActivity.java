package com.idan_koren_israeli.sailtracker.search;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.common.BaseActivity;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.common.ProfileFragment;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnGalleryPhotoLoadListener;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnMemberLoadListener;
import com.idan_koren_israeli.sailtracker.gallery.GalleryPhoto;
import com.idan_koren_israeli.sailtracker.gallery.PhotoCollectionFragment;

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
        public boolean onQueryTextSubmit(String s) {
            MemberDataManager.getInstance().loadMemberByPhone(s, onMemberLoad);
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
            if(memberLoaded!= MemberDataManager.getInstance().getCurrentUser()) {
                MemberDataManager.getInstance().loadGallery(memberLoaded.getUid(), photoLoaded);
                profile.setMember(memberLoaded);
            }
            else
                CommonUtils.getInstance().showToast("You can see your own photos in your own Gallery!");
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