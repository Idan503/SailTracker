package com.idan_koren_israeli.sailtracker.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idan_koren_israeli.sailtracker.R;

/**
 * This card present the next planned sail that the user purchased
 *
 */
public class NextSailFragment extends Fragment {

    public NextSailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NextSailCard.
     */
    // TODO: Rename and change types and number of parameters
    public static NextSailFragment newInstance(String param1, String param2) {
        NextSailFragment fragment = new NextSailFragment();
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
        return inflater.inflate(R.layout.fragment_next_sail_card, container, false);
    }
}