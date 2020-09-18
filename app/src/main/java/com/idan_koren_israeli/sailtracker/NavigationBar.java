package com.idan_koren_israeli.sailtracker;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.idan_koren_israeli.sailtracker.activities.BaseActivity;
import com.idan_koren_israeli.sailtracker.activities.CalenderActivity;
import com.idan_koren_israeli.sailtracker.activities.GalleryActivity;
import com.idan_koren_israeli.sailtracker.activities.HomeActivity;
import com.idan_koren_israeli.sailtracker.activities.SearchActivity;

/**
 * Navigator Bar: This fragment will be re-used in the apps activities
 *  User will press an icon that is shown in this bar to switch between activities
 *
 * A simple {@link Fragment} subclass.
 * Use the {@link NavigationBar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavigationBar extends Fragment {


    private LinearLayout home, calender, gallery, search;
    private BaseActivity currentActivity; // Parent activity of the fragment


    public NavigationBar() {
        // Required empty public constructor
    }

    public static NavigationBar newInstance() {
        return new NavigationBar();
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        try {
            currentActivity = (BaseActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BaseActivity");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_navigation_bar, container, false);

        findViews(parent);
        attachListeners();
        return parent;
    }


    // region Initialization Methods

    private void findViews(View parent){
        home = parent.findViewById(R.id.navigation_LAY_home);
        calender = parent.findViewById(R.id.navigation_LAY_calender);
        gallery = parent.findViewById(R.id.navigation_LAY_gallery);
        search = parent.findViewById(R.id.navigation_LAY_search);
    }

    private void attachListeners(){
        home.setOnClickListener(homeListener);
        calender.setOnClickListener(calenderListener);
        gallery.setOnClickListener(galleryListener);
        search.setOnClickListener(searchListener);
    }

    // endregion


    // region Listeners

    /*
    Each listener will change the activity only when it isn't the current one.
    The key here is to prevent an unnecessary activity recreation.
    */

    private View.OnClickListener homeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(isOtherActivity(HomeActivity.class)){
                Intent intent = new Intent(currentActivity, HomeActivity.class);
                startActivity(intent);
                currentActivity.finish();
            }
        }
    };

    private View.OnClickListener calenderListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(isOtherActivity(CalenderActivity.class)){
                Intent intent = new Intent(currentActivity, CalenderActivity.class);
                startActivity(intent);
                currentActivity.finish();
            }
        }
    };

    private View.OnClickListener galleryListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(isOtherActivity(GalleryActivity.class)){
                Intent intent = new Intent(currentActivity, GalleryActivity.class);
                startActivity(intent);
                currentActivity.finish();
            }
        }
    };

    private View.OnClickListener searchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(isOtherActivity(CalenderActivity.class)){
                Intent intent = new Intent(currentActivity, SearchActivity.class);
                startActivity(intent);
                currentActivity.finish();
            }
        }
    };

    // endregion


    // region Other Methods

    // Compares a given base activity to the current activity
    private boolean isOtherActivity(Class<? extends BaseActivity> activity){
        return (currentActivity.getClass() != activity);
    }

    // endregion
}