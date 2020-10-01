package com.idan_koren_israeli.sailtracker.calendar;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.common.BaseActivity;
import com.idan_koren_israeli.sailtracker.firebase.MembersDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.onCheckFinished;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CalendarActivity extends BaseActivity {

    RecyclerView todayEvents;
    EventsRecyclerAdapter eventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        findViews();
        //inflate: getSupportFragmentManager().beginTransaction().replace(R.id.calendar_LAY_add_event_placeholder, new AddEventFragment()).commit();

        MembersDataManager.getInstance().isManagerMember(new onCheckFinished() {
            @Override
            public void onCheckFinished(boolean result) {
                initEventsList(result);
            }
        });
    }

    private void initEventsList(boolean managerViewMode){

        // Getting the data (Should be by a date, from firebase)
        ArrayList<Event> events = new ArrayList<Event>();
        events.add(new Event("Event 1","This is an event", DateTime.now(), Minutes.minutes(50)));
        events.add(new Event("Event 2","This is an event 2", DateTime.now(), Minutes.minutes(80)));
        events.add(new Event("Event 3","This is an event 3", DateTime.now(), Minutes.minutes(120)));

        todayEvents.setLayoutManager(new LinearLayoutManager(this));
        eventsAdapter = new EventsRecyclerAdapter(this, events, managerViewMode);
        // adapter.setClickListener
        todayEvents.setAdapter(eventsAdapter);
    }

    onCheckFinished isMember = new onCheckFinished() {
        @Override
        public void onCheckFinished(boolean result) {
            System.out.println(result);
        }
    };


    private void findViews(){
        todayEvents = findViewById(R.id.calendar_RCY_daily_events);


    }
}