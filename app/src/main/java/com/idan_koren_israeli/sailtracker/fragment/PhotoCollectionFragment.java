package com.idan_koren_israeli.sailtracker.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.activity.PhotoInspectActivity;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.club.GalleryPhoto;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.view_holder.listener.OnPhotoClickedListener;
import com.idan_koren_israeli.sailtracker.adapter.PhotoCollectionAdapter;
import com.idan_koren_israeli.sailtracker.club.comparator.SortByCreationTime;

import java.util.ArrayList;

/**
 *
 * This fragment is used to be like Instagram feed
 * Each user has an associated photos from sails
 *
 *
 */
public class PhotoCollectionFragment extends Fragment {

    ClubMember member;
    RecyclerView recyclerView;
    LoadingFragment loadingFragment;
    PhotoCollectionAdapter adapter;


    private static final int NUM_OF_COLUMNS = 3;

    public PhotoCollectionFragment() {
        // Required empty public constructor
    }


    public void setMember(ClubMember member){
        this.member = member;
        updateUI();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_photo_collection, container, false);
        findViews(parent);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), NUM_OF_COLUMNS));
        adapter = new PhotoCollectionAdapter(getContext());
        return parent;
    }

    private void findViews(View parent){
        recyclerView = parent.findViewById(R.id.photo_collection_RCY_recycler);
        loadingFragment = (LoadingFragment) getChildFragmentManager().findFragmentById(R.id.photo_collection_FRAG_loading);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI(){
        adapter = new PhotoCollectionAdapter(getContext(),getMemberPhotos());
        adapter.setPhotoClickListener(onPhotoClicked);
        recyclerView.setAdapter(adapter);
    }

    private OnPhotoClickedListener onPhotoClicked = new OnPhotoClickedListener() {
        @Override
        public void onPhotoClicked(GalleryPhoto photo) {
            boolean deletablePhoto = (member == MemberDataManager.getInstance().getCurrentUser());
            // Photos can be deleted via inspect activity iff member owns this collection (current user)

            Intent intent = new Intent(getActivity(), PhotoInspectActivity.class);
            intent.putExtra(PhotoInspectActivity.KEYS.PHOTO_OBJ,photo);
            intent.putExtra(PhotoInspectActivity.KEYS.SHOW_DELETE_BUTTON, deletablePhoto);
            startActivity(intent);
        }
    };

    private ArrayList<GalleryPhoto> getMemberPhotos(){
        if(member==null)
            return null;
        ArrayList<GalleryPhoto> photos = new ArrayList<>();
        if(member.getGalleryPhotos()!=null)
            photos.addAll(member.getGalleryPhotos());
        photos.sort(new SortByCreationTime());
        return photos;
    }

    public void hideLoading(){
        loadingFragment.hide();
    }

    public void showLoading(){
        loadingFragment.show();
    }




}