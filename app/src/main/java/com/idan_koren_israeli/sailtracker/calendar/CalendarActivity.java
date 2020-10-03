package com.idan_koren_israeli.sailtracker.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.common.BaseActivity;
import com.idan_koren_israeli.sailtracker.firebase.EventDataManager;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnCheckFinished;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnEventsLoaded;

import org.joda.time.LocalDate;

import java.util.ArrayList;

public class CalendarActivity extends BaseActivity {

    private CalendarView calendar;
    private RecyclerView events;
    private TextView dateTitle;
    private LocalDate selectedDate = LocalDate.now();
    private ArrayList<Event> eventsToShow = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        findViews();
        initAddedEvent();


        calendar.setOnDateChangeListener(onDateChangeListener);
        reloadData();
    }

    // This method will find the added event if manager-user gets back from @AddEventActivity
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
    private void initEventsList(boolean isCurrentUserManager){
        events.setLayoutManager(new LinearLayoutManager(this));

        EventsRecyclerAdapter eventsAdapter;
        if(isCurrentUserManager){
            // manager layout - with "add" button as last view
            eventsAdapter = new ManagerEventRecyclerAdapter(this, eventsToShow);
            ((ManagerEventRecyclerAdapter) eventsAdapter).setAddClickedListener(onClickedAddButton);
        }
        else{
            // a regular recycler view of all of today's events
            eventsAdapter = new EventsRecyclerAdapter(this, eventsToShow);
        }

        eventsAdapter.setOnPurchasePressed(onClickedPurchase);
        events.setAdapter(eventsAdapter);
    }

    private void findViews(){
        events = findViewById(R.id.calendar_RCY_daily_events);
        calendar = findViewById(R.id.calendar_CALENDAR);
        dateTitle = findViewById(R.id.calendar_LBL_selected_day);

    }

    // Loading data from database based on current day that is selected in calendar view
    private void reloadData(){
        onDateChangeListener.onSelectedDayChange(calendar,
                selectedDate.getYear(), selectedDate.getMonthOfYear()-1, selectedDate.getDayOfMonth());
    }

    //region UI Direct Callbacks
    private View.OnClickListener onClickedPurchase = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //purchase system...
        }
    };


    private View.OnClickListener onClickedAddButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // load add menu fragment....
            Intent intent = new Intent(CalendarActivity.this, AddEventActivity.class);
            intent.putExtra(AddEventActivity.KEYS.EVENT_DATE, selectedDate);
            startActivity(intent);
            finish();
        }
    };

    //endregion

    //region Realtime Database callbacks


    private OnEventsLoaded onEventsLoaded = new OnEventsLoaded() {
        @Override
        public void onEventsListener(ArrayList<Event> eventsLoaded) {
            eventsToShow.clear();
            eventsToShow.addAll(eventsLoaded);
            MemberDataManager.getInstance().isManagerMember(onCheckedManager);
            // Should Hide loading menu here
        }
    };

    private OnCheckFinished onCheckedManager = new OnCheckFinished() {
        @Override
        public void onCheckFinished(boolean result) {
            // Code gets to here after we checked with the db if the current user is a manager or not
            // Therefore, we will know if our recycler view should inflate another last view of "Add Event Button"
            initEventsList(result);
        }
    };



    //endregion

    //region Calendar Callbacks

    CalendarView.OnDateChangeListener onDateChangeListener = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
            LocalDate dateSelected = new LocalDate(i,i1+1,i2);
            dateTitle.setText(dateSelected.toString());
            selectedDate = dateSelected;
            EventDataManager.getInstance().loadEvents(dateSelected, onEventsLoaded);
            // Should show loading screen
        }
    };


    //endregion
}