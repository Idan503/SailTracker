package com.idan_koren_israeli.sailtracker.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.exceptions.AlreadyRegisteredException;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.exceptions.EventFullException;
import com.idan_koren_israeli.sailtracker.club.exceptions.NotEnoughPointsException;
import com.idan_koren_israeli.sailtracker.common.BaseActivity;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.common.LoadingFragment;
import com.idan_koren_israeli.sailtracker.common.PointsStatusFragment;
import com.idan_koren_israeli.sailtracker.event_recycler.adapter.ManagerEventRecyclerAdapter;
import com.idan_koren_israeli.sailtracker.event_recycler.OnEventClickedListener;
import com.idan_koren_israeli.sailtracker.event_recycler.adapter.RegistrableEventRecyclerAdapter;
import com.idan_koren_israeli.sailtracker.firebase.EventDataManager;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnCheckFinishedListener;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnListLoadedListener;

import org.joda.time.LocalDate;

import java.util.ArrayList;

public class CalendarActivity extends BaseActivity {

    private CalendarView calendar;
    private RecyclerView events;
    private TextView dateTitle;
    private LocalDate selectedDate = LocalDate.now();
    private ArrayList<Event> eventsToShow = new ArrayList<>();
    private PointsStatusFragment pointsStatus;
    private ClubMember currentUser;

    private boolean managerView = false;
    private LoadingFragment loadingFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        currentUser = MemberDataManager.getInstance().getCurrentUser();
        findViews();
        initAddedEvent();


        calendar.setOnDateChangeListener(onDateChangeListener);
        reloadData();
    }

    // Finds the added event if manager-user gets back from @AddEventActivity
    private void initAddedEvent() {
        Event addedEvent = null;
        Intent intent = getIntent();
        if(intent.hasExtra(AddEventActivity.KEYS.ADDED_EVENT)){
            addedEvent = (Event) intent.getSerializableExtra(AddEventActivity.KEYS.ADDED_EVENT);
        }

        if(addedEvent!=null) {
            EventDataManager.getInstance().storeEvent(addedEvent);
            selectedDate = addedEvent.getStartDateTime().toLocalDate();
            calendar.setDate(selectedDate.toDateTimeAtStartOfDay().getMillis());
            // Adding the new event to db and setting the calendar date to show it
        }

    }


    // Loading the events into ui, parameter for manager/normal user view mode
    private void initEventsList(boolean isCurrentUserManager, ArrayList<Event> registered){
        events.setLayoutManager(new LinearLayoutManager(this));

        Log.i("pttt", "Registered Events : " + registered);
        RegistrableEventRecyclerAdapter eventsAdapter;
        if(isCurrentUserManager){
            // manager layout - with "add" button as last view
            eventsAdapter = new ManagerEventRecyclerAdapter(this, eventsToShow, registered);
            ((ManagerEventRecyclerAdapter) eventsAdapter).setAddClickedListener(onClickedAddButton);
        }
        else{
            // a regular recycler view of all of today's events
            eventsAdapter = new RegistrableEventRecyclerAdapter(this, eventsToShow, registered);
        }

        eventsAdapter.setButtonsListeners(onRegisterClicked, onUnregisterClicked);
        events.setAdapter(eventsAdapter);
        loadingFragment.hide();
    }

    private void findViews(){
        events = findViewById(R.id.calendar_RCY_daily_events);
        calendar = findViewById(R.id.calendar_CALENDAR);
        dateTitle = findViewById(R.id.calendar_LBL_selected_day);
        loadingFragment = (LoadingFragment) getSupportFragmentManager().findFragmentById(R.id.calendar_FRAG_loading);
        pointsStatus = (PointsStatusFragment) getSupportFragmentManager().findFragmentById(R.id.calendar_FRAG_points_status);

    }

    // Loading data from database based on current day that is selected in calendar view
    private void reloadData(){
        pointsStatus.updateCount(currentUser.getPointsCount());
        onDateChangeListener.onSelectedDayChange(calendar,
                selectedDate.getYear(), selectedDate.getMonthOfYear()-1, selectedDate.getDayOfMonth());
    }

    //region UI Related Callbacks
    private OnEventClickedListener onRegisterClicked = new OnEventClickedListener() {
        @Override
        public void onButtonClicked(Event eventClicked) {
            try {
                EventDataManager.getInstance().registerMember(currentUser, eventClicked);
                CommonUtils.getInstance().showToast("Registered successfully!");
                reloadData();
            }
            catch (EventFullException eventFull){
                Log.e("CalendarActivity", eventFull.toString());
                CommonUtils.getInstance().showToast("Register Failed - Event is full.");
            }
            catch (NotEnoughPointsException notEnoughPoints){
                Log.e("CalendarActivity", notEnoughPoints.toString());
                CommonUtils.getInstance().showToast("Register Failed - Not enough points.");
            }
            catch (AlreadyRegisteredException alreadyRegistered){
                Log.e("CalendarActivity", alreadyRegistered.toString());
                CommonUtils.getInstance().showToast("Member is already registered.");
            }

        }
    };

    private OnEventClickedListener onUnregisterClicked = new OnEventClickedListener(){
        @Override
        public void onButtonClicked(Event eventClicked) {
            EventDataManager.getInstance().unregisterMember(currentUser, eventClicked);
            reloadData();
        }
    };

    private View.OnClickListener onClickedAddButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(CalendarActivity.this, AddEventActivity.class);
            intent.putExtra(AddEventActivity.KEYS.EVENT_DATE, selectedDate);
            startActivity(intent);
            finish();
        }
    };

    //endregion

    //region Realtime Database callbacks


    private OnListLoadedListener<Event> onEventsLoaded = new OnListLoadedListener<Event>() {
        @Override
        public void onListLoaded(ArrayList<Event> eventsLoaded) {
            eventsToShow.clear();
            eventsToShow.addAll(eventsLoaded);
            MemberDataManager.getInstance().isManagerMember(onCheckedManager);
        }
    };

    private OnCheckFinishedListener onCheckedManager = new OnCheckFinishedListener() {
        @Override
        public void onCheckFinished(boolean result) {
            // Code gets to here after we checked with the db if the current user is a manager or not
            // Therefore, we will know if our recycler view should inflate another last view of "Add Event Button"
            managerView = result;
            EventDataManager.getInstance().loadRegisteredEvents(onListLoadedListener);
        }
    };

    private OnListLoadedListener<Event> onListLoadedListener = new OnListLoadedListener<Event>() {
        @Override
        public void onListLoaded(ArrayList<Event> list) {
            // Code gets to here after we already know if the current user is a manager or not.
            // And also the list of the current user's member already registered events is loaded.
            initEventsList(managerView, list);
        }
    };



    //endregion

    //region Calendar Callbacks

    CalendarView.OnDateChangeListener onDateChangeListener = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
            loadingFragment.show();
            LocalDate dateSelected = new LocalDate(i,i1+1,i2);
            dateTitle.setText(dateSelected.toString());
            selectedDate = dateSelected;
            EventDataManager.getInstance().loadEventsByDate(dateSelected, onEventsLoaded);
        }
    };


    //endregion
}