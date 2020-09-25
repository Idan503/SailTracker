package com.idan_koren_israeli.sailtracker.gallery;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idan_koren_israeli.sailtracker.R;

/**
 *
 * This fragment is used to be like Instagram feed
 * Each user has an associated photos from sails
 *
 *
 */
public class PhotoCollectionFragment extends Fragment {


    public PhotoCollectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PhotoCluster.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoCollectionFragment newInstance(String param1, String param2) {
        PhotoCollectionFragment fragment = new PhotoCollectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_collection, container, false);
    }
}