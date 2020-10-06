package com.idan_koren_israeli.sailtracker.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.fragment.LoadingFragment;
import com.idan_koren_israeli.sailtracker.adapter.SeparatedEventRecyclerAdapter;
import com.idan_koren_israeli.sailtracker.firebase.EventDataManager;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnListLoadedListener;

import java.util.ArrayList;


/**
 * Shows information on all user's registered past and future sails and events
 * Ordered by time, while the reciyclerview will start by default as showing the future events on screen.
 * Therefore, past events will be shown to the user when he will swipe to the upper section of the recycler
 *
 */
public class HistoryActivity extends BaseActivity {

    private FloatingActionButton backButton;
    private RecyclerView recyclerView;
    private ClubMember currentUser;
    private LoadingFragment loadingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sails);
        findViews();
        setListeners();

        currentUser = MemberDataManager.getInstance().getCurrentUser();

        EventDataManager.getInstance().loadRegisteredEvents(onEventsLoaded);


    }

    private OnListLoadedListener<Event> onEventsLoaded = new OnListLoadedListener<Event>() {

        @Override
        public void onListLoaded(ArrayList<Event> list) {
            initEventsList(list);
        }
    };

    private void initEventsList(ArrayList<Event> myEvents){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SeparatedEventRecyclerAdapter eventsAdapter;
        eventsAdapter = new SeparatedEventRecyclerAdapter(this, myEvents);

        recyclerView.setAdapter(eventsAdapter);
        recyclerView.scrollToPosition(eventsAdapter.getFutureTitlePosition());

        loadingFragment.hide();
        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int x, int y, int oldX, int oldY) {
                if(y >= oldY){
                    backButton.setVisibility(View.GONE);
                }
                else
                    backButton.setVisibility(View.VISIBLE);
            }
        });
    }


    private void setListeners(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Back to the caller activity, which is not finished
                finish();
            }
        });
    }

    private void findViews(){
        backButton = findViewById(R.id.my_sails_FAB_back);
        recyclerView = findViewById(R.id.my_sails_RYC_recycler);
        loadingFragment = (LoadingFragment) getSupportFragmentManager().findFragmentById(R.id.my_sails_FRAG_loading);
    }
}