package com.idan_koren_israeli.sailtracker.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.fragment.LoadingFragment;
import com.idan_koren_israeli.sailtracker.adapter.SeparatedEventRecyclerAdapter;
import com.idan_koren_israeli.sailtracker.firebase.EventDataManager;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnListLoadedListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Shows information on all user's registered past and future sails and events
 * Ordered by time, while the reciyclerview will start by default as showing the future events on screen.
 * Therefore, past events will be shown to the user when he will swipe to the upper section of the recycler
 *
 */
public class HistoryActivity extends BaseActivity {

    public interface KEYS {
        String MEMBER_TO_SHOW = "member_to_show";
    }

    private FloatingActionButton backButton;
    private RecyclerView recyclerView;
    private ClubMember member;
    private LoadingFragment loadingFragment;

    private boolean backButtonShowing = true;
    private final int BACK_ANIMATION_DURATION = 450; //in ms

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        findViews();
        setListeners();
        initMemberToShow();

        EventDataManager.getInstance().loadRegisteredEvents(member,onEventsLoaded);


    }

    private void initMemberToShow(){
        member = (ClubMember) getIntent().getSerializableExtra(KEYS.MEMBER_TO_SHOW);
        if(member==null){
            CommonUtils.getInstance().showToast("Member could not be found");
            finish();
        }
    }

    private OnListLoadedListener<Event> onEventsLoaded = new OnListLoadedListener<Event>() {
        @Override
        public void onListLoaded(List<Event> list) {
            initEventsList(list);
        }
    };

    private void initEventsList(List<Event> myEvents){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SeparatedEventRecyclerAdapter eventsAdapter;
        eventsAdapter = new SeparatedEventRecyclerAdapter(this, myEvents);

        recyclerView.setAdapter(eventsAdapter);
        recyclerView.scrollToPosition(eventsAdapter.getFutureTitlePosition());

        loadingFragment.hide();
        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int x, int y, int oldX, int oldY) {
                // FAB will be shown after swipe up or at the bottom scroll for better ux

                if(!recyclerView.canScrollVertically(1)){
                    // Bottom of recycler
                    if(!backButtonShowing)
                        showBackButton();
                }
                else if(y > oldY) {
                    if(backButtonShowing)
                        hideBackButton();
                }
                else{
                    if(!backButtonShowing)
                        showBackButton();
                }
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


    //region Back Button Animation
    private void showBackButton(){
        backButtonShowing = true;
        backButton.setScaleX(0.0f);
        backButton.setScaleY(0.0f);
        backButton.setAlpha(0.0f);
        backButton.animate()
                .alpha(1.0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(BACK_ANIMATION_DURATION)
                .setInterpolator(new AnticipateOvershootInterpolator());
    }


    private void hideBackButton(){
        backButtonShowing = false;
        backButton.animate()
                .alpha(0.0f)
                .scaleX(0.0f)
                .scaleY(0.0f)
                .setDuration(BACK_ANIMATION_DURATION)
                .setInterpolator(new AnticipateOvershootInterpolator());
    }
    //endregion
}