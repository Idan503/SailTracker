package com.idan_koren_israeli.sailtracker.activity;

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
import com.idan_koren_israeli.sailtracker.club.exception.AlreadyRegisteredException;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.exception.EventFullException;
import com.idan_koren_israeli.sailtracker.club.exception.NotEnoughPointsException;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.common.SharedPrefsManager;
import com.idan_koren_israeli.sailtracker.fragment.LoadingFragment;
import com.idan_koren_israeli.sailtracker.fragment.PointsStatusFragment;
import com.idan_koren_israeli.sailtracker.adapter.EventRecyclerAdapter;
import com.idan_koren_israeli.sailtracker.adapter.ManagerEventRecyclerAdapter;
import com.idan_koren_israeli.sailtracker.notification.EventWatchManager;
import com.idan_koren_israeli.sailtracker.notification.EventWatchService;
import com.idan_koren_israeli.sailtracker.view_holder.listener.OnEventClickedListener;
import com.idan_koren_israeli.sailtracker.adapter.RegistrableEventRecyclerAdapter;
import com.idan_koren_israeli.sailtracker.firebase.EventDataManager;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnCheckFinishedListener;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnListLoadedListener;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class CalendarActivity extends BaseActivity {

    private CalendarView calendar;
    private TextView dateTitle;
    private LocalDate selectedDate = LocalDate.now();
    private ArrayList<Event> eventsToShow = new ArrayList<>();
    private PointsStatusFragment pointsStatus;
    private ClubMember currentMember;

    private RecyclerView eventsRecycler;
    private EventRecyclerAdapter eventsAdapter;

    private EventWatchManager serviceManager;

    private boolean managerView = false;
    private LoadingFragment loadingFragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        currentMember = MemberDataManager.getInstance().getCurrentMember();
        serviceManager = EventWatchManager.initHelper(this);
        findViews();
        initAddedEvent();


        calendar.setOnDateChangeListener(onDateChangeListener);
        recreateData();
    }

    // Finds the added event if manager-user gets back from @AddEventActivity
    private void initAddedEvent() {
        Event addedEvent = null;
        Intent intent = getIntent();
        if(intent.hasExtra(AddEventActivity.KEYS.ADDED_EVENT)){
            addedEvent = (Event) intent.getSerializableExtra(AddEventActivity.KEYS.ADDED_EVENT);
        }

        if(addedEvent!=null) {
            // Back from @AddEventActivity
            EventDataManager.getInstance().storeEvent(addedEvent);
            selectedDate = addedEvent.getStartDateTime().toLocalDate();
            calendar.setDate(selectedDate.toDateTimeAtStartOfDay().getMillis());
            // Adding the new event to db and setting the calendar date to show it
        }

    }


    // Loading the events into ui, parameter for manager/normal user view mode
    private void initEventsList(boolean isCurrentUserManager, List<Event> registered) {
        eventsRecycler.setLayoutManager(new LinearLayoutManager(this));


        if (selectedDate.toDateTimeAtStartOfDay().plusDays(1).getMillis() < DateTime.now().getMillis()) {
            // Selected date is in the past, so there should not be an option to add/register to events
            eventsAdapter = new EventRecyclerAdapter(this, eventsToShow,false);
        } else {
            if (isCurrentUserManager) {
                // manager layout - with "add" button as last view
                eventsAdapter = new ManagerEventRecyclerAdapter(this, eventsToShow, registered);
                ((ManagerEventRecyclerAdapter) eventsAdapter).setAddClickedListener(onClickedAddButton);
            } else {
                // a register recycler view of all of selected day's events
                eventsAdapter = new RegistrableEventRecyclerAdapter(this, eventsToShow, registered);
            }
            ((RegistrableEventRecyclerAdapter)eventsAdapter)
                    .setButtonsListeners(onRegisterClicked, onUnregisterClicked,
                                         onWatchClicked, onUnwatchClicked);
        }



        eventsRecycler.setAdapter(eventsAdapter);
        loadingFragment.hide();
    }

    private void findViews(){
        eventsRecycler = findViewById(R.id.calendar_RCY_daily_events);
        calendar = findViewById(R.id.calendar_CALENDAR);
        dateTitle = findViewById(R.id.calendar_LBL_selected_day);
        loadingFragment = (LoadingFragment) getSupportFragmentManager().findFragmentById(R.id.calendar_FRAG_loading);
        pointsStatus = (PointsStatusFragment) getSupportFragmentManager().findFragmentById(R.id.calendar_FRAG_points_status);

    }

    // Loading data from database based on current day that is selected in calendar view
    private void recreateData(){
        reloadPointsStatus();
        onDateChangeListener.onSelectedDayChange(calendar,
                selectedDate.getYear(), selectedDate.getMonthOfYear()-1, selectedDate.getDayOfMonth());
    }

    private void reloadPointsStatus(){
        pointsStatus.updateCount(currentMember.getPointsCount());
    }


    // Members can't register to 2 events that has a common shared time
    // Checking if a given event can be registered by checking time overlap with the others
    private boolean canMemberRegister(Event registerEvent){
        for(Event event : eventsToShow){
            if(registerEvent!=event && event.getRegisteredMembers().contains(currentMember.getUid())){
                if(registerEvent.getStartTime() < event.getEndTime()  && event.getStartTime() < registerEvent.getEndTime()){
                    return false; // member is already register to an overlapping event
                }
            }
        }
        return true;
    }


    //region UI Related Callbacks
    private OnEventClickedListener onRegisterClicked = new OnEventClickedListener() {
        @Override
        public void onButtonClicked(Event eventClicked) {
            try {
                if(canMemberRegister(eventClicked)) {
                    EventDataManager.getInstance().registerMember(currentMember, eventClicked);
                    reloadPointsStatus();
                    CommonUtils.getInstance().showToast("Registered successfully!");


                }
                else {
                    CommonUtils.getInstance().showToast("Already registered at the same time");
                }
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
            EventDataManager.getInstance().unregisterMember(currentMember, eventClicked);
            reloadPointsStatus();
            CommonUtils.getInstance().showToast("Unregistered successfully!");
        }
    };

    private OnEventClickedListener onWatchClicked = new OnEventClickedListener() {
        @Override
        public void onButtonClicked(Event eventClicked) {
            SharedPrefsManager sp = SharedPrefsManager.getInstance();
            sp.putString(SharedPrefsManager.KEYS.WATCH_EVENT_ID, eventClicked.getEid());
            initEventWatchService(eventClicked);

        }
    };

    private OnEventClickedListener onUnwatchClicked = new OnEventClickedListener() {
        @Override
        public void onButtonClicked(Event eventClicked) {
            SharedPrefsManager sp = SharedPrefsManager.getInstance();
            sp.removeKey(SharedPrefsManager.KEYS.WATCH_EVENT_ID);
            cancelEventWatchService();
        }
    };


    private void initEventWatchService(Event eventToWatch){
        serviceManager.startWatch(eventToWatch);
    }

    private void cancelEventWatchService(){
        serviceManager.stopWatch();
    }



    private View.OnClickListener onClickedAddButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(CalendarActivity.this, AddEventActivity.class);
            intent.putExtra(AddEventActivity.KEYS.EVENT_DATE, selectedDate);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serviceManager.destroyService();
    }

    //endregion


    //region Realtime Database callbacks


    private OnListLoadedListener<Event> onEventsLoaded = new OnListLoadedListener<Event>() {
        @Override
        public void onListLoaded(List<Event> eventsLoaded) {
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
        public void onListLoaded(List<Event> list) {
            // Code gets to here after we already know if the current user is a manager or not.
            // And also the list of the current user's member already registered events is loaded.
            initEventsList(managerView, list);
            // Using realtime-events to update recycler view across all devices
        }
    };



    //endregion

    //region Calendar Callbacks

    CalendarView.OnDateChangeListener onDateChangeListener = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
            loadingFragment.show();
            LocalDate dateSelected = new LocalDate(i,i1+1,i2);
            dateTitle.setText(dateSelected.toString("dd.MM.YYYY"));
            selectedDate = dateSelected;
            EventDataManager.getInstance().loadEventsByDate(dateSelected, onEventsLoaded);
        }
    };


    //endregion
}