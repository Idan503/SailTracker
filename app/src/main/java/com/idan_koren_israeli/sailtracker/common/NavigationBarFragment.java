package com.idan_koren_israeli.sailtracker.common;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.calendar.CalendarActivity;
import com.idan_koren_israeli.sailtracker.gallery.GalleryActivity;
import com.idan_koren_israeli.sailtracker.home.HomeActivity;
import com.idan_koren_israeli.sailtracker.search.SearchActivity;

/**
 * Navigator Bar: This fragment will be re-used in the apps activities
 *  User will press an icon that is shown in this bar to switch between activities
 *  This fragment is made for the 4 activities of this application
 */
public class NavigationBarFragment extends Fragment {

    private ViewGroup parent;
    private LinearLayout home, calendar, gallery, search; // Activities options
    private BaseActivity currentActivity; // Parent activity of the fragment



    public NavigationBarFragment() {
        // Required empty public constructor
    }

    public static NavigationBarFragment newInstance() {
        return new NavigationBarFragment();
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
        parent = (ViewGroup) inflater.inflate(R.layout.fragment_navigation_bar, container, false);

        findViews(parent);
        attachListeners();
        setIconColors();
        return parent;
    }


    // region Initialization Methods

    private void findViews(View parent){
        home = parent.findViewById(R.id.navigation_LAY_home);
        calendar = parent.findViewById(R.id.navigation_LAY_calender);
        gallery = parent.findViewById(R.id.navigation_LAY_gallery);
        search = parent.findViewById(R.id.navigation_LAY_search);
    }

    private void attachListeners(){
        home.setOnClickListener(homeListener);
        calendar.setOnClickListener(calendarListener);
        gallery.setOnClickListener(galleryListener);
        search.setOnClickListener(searchListener);
    }

    // Assigns colors to the navigator icons, a different color for current activity
    private void setIconColors(){
        LinearLayout selectedIcon = home; // this will be the icon of the current activity

        // When current activity is not home, currentIcon would be changed
        if(isCurrentActivity(CalendarActivity.class)){
            selectedIcon = calendar;
        }else if(isCurrentActivity(GalleryActivity.class)){
            selectedIcon = gallery;
        }else if(isCurrentActivity(SearchActivity.class)){
            selectedIcon = search;
        }

        // Now we set each icon color, selected color for current activity icon (selectedIcon)
        for(int i=0;i<parent.getChildCount();i++){
            View icon = parent.getChildAt(i);

            // Getting teh views which the icon contains
            ImageView image = icon.findViewById(R.id.navigation_icon_IMG_image);
            TextView text = icon.findViewById(R.id.navigation_icon_LBL_text);

            if(icon == selectedIcon) {
                image.setColorFilter(ContextCompat.getColor(parent.getContext(), R.color.navigator_selected_ic));
                text.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.navigator_selected_ic));
            }
            else {
                image.setColorFilter(ContextCompat.getColor(parent.getContext(), R.color.navigator_ic));
                text.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.navigator_ic));
            }

        }

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
            if(!isCurrentActivity(HomeActivity.class)){
                Intent intent = new Intent(currentActivity, HomeActivity.class);
                startActivity(intent);
                currentActivity.finish();
            }
        }
    };

    private View.OnClickListener calendarListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!isCurrentActivity(CalendarActivity.class)){
                Intent intent = new Intent(currentActivity, CalendarActivity.class);
                startActivity(intent);
                currentActivity.finish();
            }
        }
    };

    private View.OnClickListener galleryListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!isCurrentActivity(GalleryActivity.class)){
                Intent intent = new Intent(currentActivity, GalleryActivity.class);
                startActivity(intent);
                currentActivity.finish();
            }
        }
    };

    private View.OnClickListener searchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!isCurrentActivity(SearchActivity.class)){
                Intent intent = new Intent(currentActivity, SearchActivity.class);
                startActivity(intent);
                currentActivity.finish();
            }
        }
    };

    // endregion


    // region Other Methods

    // Compares a given base activity to the current activity
    private boolean isCurrentActivity(Class<? extends BaseActivity> activity){
        return (currentActivity.getClass() == activity);
    }

    // endregion
}