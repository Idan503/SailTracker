package com.idan_koren_israeli.sailtracker.calendar;

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

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;

public class CalendarActivity extends BaseActivity {

    private CalendarView calendar;
    private RecyclerView events;
    private TextView dateTitle;
    private LocalDate currentDate = LocalDate.now();
    private AddEventFragment addEventFragment;
    private ArrayList<Event> eventsToShow = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        findViews();


        calendar.setOnDateChangeListener(onDateChangeListener);
        reloadData();
    }


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
                currentDate.getYear(), currentDate.getMonthOfYear()-1, currentDate.getDayOfMonth());
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
            // load add fragment....
            addEventFragment = new AddEventFragment();
            addEventFragment.setDate(currentDate);
            addEventFragment.setOnEventAddedListener(onEventAdded);
            getSupportFragmentManager().beginTransaction().replace(R.id.calendar_LAY_add_event_placeholder, addEventFragment).commit();

        }
    };

    //endregion

    //region Realtime Database callbacks

    private OnEventAdded onEventAdded = new OnEventAdded() {
        @Override
        public void onEventAdded(Event event) {
            EventDataManager.getInstance().storeEvent(event);
            getSupportFragmentManager().beginTransaction().hide(addEventFragment).commit();
            // reload current
            calendar.setDate(DateTime.now().getMillis());
            reloadData();
        }
    };

    private OnEventsLoaded onEventsLoaded = new OnEventsLoaded() {
        @Override
        public void onEventsListener(ArrayList<Event> eventsLoaded) {
            eventsToShow.clear();
            eventsToShow.addAll(eventsLoaded);
            MemberDataManager.getInstance().isManagerMember(onCheckedManager);
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
            currentDate = dateSelected;
            EventDataManager.getInstance().loadEvents(dateSelected, onEventsLoaded);
        }
    };


    //endregion
}