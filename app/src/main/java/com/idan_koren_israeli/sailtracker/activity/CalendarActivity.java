package com.idan_koren_israeli.sailtracker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.Distribution;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.adapter.ManagerEventRecyclerAdapter;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.common.SharedPrefsManager;
import com.idan_koren_israeli.sailtracker.fragment.LoadingFragment;
import com.idan_koren_israeli.sailtracker.fragment.PointsStatusFragment;
import com.idan_koren_israeli.sailtracker.adapter.EventRecyclerAdapter;
import com.idan_koren_israeli.sailtracker.notification.EventWatchManager;
import com.idan_koren_israeli.sailtracker.firebase.EventDataManager;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnCheckFinishedListener;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnListLoadedListener;
import com.idan_koren_israeli.sailtracker.notification.EventWatchService;
import com.idan_koren_israeli.sailtracker.recycler.CalendarRecyclerManager;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class CalendarActivity extends BaseActivity {


    private CalendarView calendar;
    private TextView dateTitle;
    private LocalDate selectedDate = LocalDate.now();
    private ArrayList<Event> eventsToShow = new ArrayList<>();

    private RecyclerView eventsRecycler;
    private CalendarRecyclerManager recyclerManager;
    private ClubMember currentMember;
    private boolean managerView = false;

    private EventWatchManager serviceManager;

    private PointsStatusFragment pointsStatus;
    private LoadingFragment loadingFragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        currentMember = MemberDataManager.getInstance().getCurrentMember();
        serviceManager = EventWatchManager.initHelper(this);

        findViews();
        eventsRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Gathering input info if exists:
        initAddedEvent();
        initWatchedEvent();

        recyclerManager = CalendarRecyclerManager.initHelper(this);

        calendar.setOnDateChangeListener(onDateChangeListener);
        recreateData();
    }


    //region Receive Information From Other Intent(s)
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

    // In case user comes from alert, we will load the calendar into the date of the wathced event
    private void initWatchedEvent(){
        SharedPrefsManager spManager = SharedPrefsManager.getInstance();
        if(spManager.contain(SharedPrefsManager.KEYS.WATCHED_EVENT)) {

            Event watchedEvent = spManager.getObject(SharedPrefsManager.KEYS.WATCHED_EVENT, Event.class);
            if (watchedEvent != null) {
                selectedDate = watchedEvent.getStartDateTime().toLocalDate();
                calendar.setDate(selectedDate.toDateTimeAtStartOfDay().getMillis());
                // Matching the event watched to the date that will be shown as loaded

                spManager.removeKey(SharedPrefsManager.KEYS.WATCHED_EVENT);
                // after one load, in the future it will not load to the same watched event date
            }
        }
    }

    //endregion


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

    public void reloadPointsStatus(){
        pointsStatus.updateCount(currentMember.getPointsCount());
    }



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
        public void onListLoaded(List<Event> registeredEvents) {
            // Code gets to here after we already know if the current user is a manager or not.
            // And also the list of the current user's member already registered events is loaded.
            recyclerManager.updateEventsList(managerView,eventsToShow,registeredEvents);
            loadingFragment.hide();
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


    //region Getters & Setters
    // For communication with @CalendarRecyclerManager
    public EventWatchManager getWatchManager()
    {
        return serviceManager;
    }

    public LocalDate getSelectedDate()
    {
        return selectedDate;
    }

    public List<Event> getEventsToShow()
    {
        return eventsToShow;
    }

    public void setRecyclerAdapter(EventRecyclerAdapter adapter)
    {
        eventsRecycler.setAdapter(adapter);
    }

    //endregion



    @Override
    protected void onDestroy() {
        super.onDestroy();
        serviceManager.destroyService();
    }

}