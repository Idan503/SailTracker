package com.idan_koren_israeli.sailtracker.gallery;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.ClubMember;

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
    PhotoCollectionAdapter adapter;

    public interface KEYS{
        String PHOTO_OBJ = "photo_obj";
    }

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
        recyclerView = parent.findViewById(R.id.photo_collection_RCY_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), NUM_OF_COLUMNS));
        return parent;
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
            Intent intent = new Intent(getActivity(), PhotoInspectActivity.class);
            intent.putExtra(KEYS.PHOTO_OBJ,photo);
            startActivity(intent);
        }
    };

    private GalleryPhoto[] getMemberPhotos(){
        if(member!=null) {
            member.getGalleryPhotos().sort(new SortByCreationTime());
            ArrayList<GalleryPhoto> photosList = member.getGalleryPhotos();
            GalleryPhoto[] photosArray = new GalleryPhoto[photosList.size()];
            return photosList.toArray(photosArray);
        }
        return new GalleryPhoto[0];
    }




}