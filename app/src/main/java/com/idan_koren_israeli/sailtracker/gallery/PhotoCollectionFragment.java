package com.idan_koren_israeli.sailtracker.gallery;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.common.ClubMember;

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

    private static final int NUM_OF_COLUMNS = 3;

    public PhotoCollectionFragment() {
        // Required empty public constructor
    }


    public static PhotoCollectionFragment newInstance(ClubMember member) {
        PhotoCollectionFragment fragment = new PhotoCollectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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


    private void updateUI(){
        adapter = new PhotoCollectionAdapter(getContext(),getMemberPhotos());
        Log.i("pttt", "Length: " + getMemberPhotos().length);
        adapter.setPhotoClickListener(onPhotoClicked);
        recyclerView.setAdapter(adapter);
    }

    private View.OnClickListener onPhotoClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    private GalleryPhoto[] getMemberPhotos(){
        ArrayList<GalleryPhoto> photosList = member.getGalleryPhotos();
        GalleryPhoto[] photosArray = new GalleryPhoto[photosList.size()];
        return photosList.toArray(photosArray);
    }


}