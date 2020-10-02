package com.idan_koren_israeli.sailtracker.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import org.joda.time.Minutes;

import java.util.ArrayList;
import java.util.UUID;

public class CalendarActivity extends BaseActivity {

    RecyclerView todayEvents;
    AddEventFragment addEventFragment;
    EventsRecyclerAdapter eventsAdapter;
    ArrayList<Event> eventsToShow = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        findViews();

        EventDataManager.getInstance().loadEvents(DateTime.now(), onEventsLoaded);

    }


    private void initEventsList(boolean isCurrentUserManager){

        todayEvents.setLayoutManager(new LinearLayoutManager(this));

        if(isCurrentUserManager){
            // manager layout - with "add" button as last view
            eventsAdapter = new ManagerEventRecyclerAdapter(this, eventsToShow);
            ((ManagerEventRecyclerAdapter) eventsAdapter).setAddClickedListener(onClickedAdd);
        }
        else{
            // a regular recycler view of all of today's events
            eventsAdapter = new EventsRecyclerAdapter(this, eventsToShow);
        }

        eventsAdapter.setOnPurchasePressed(onClickedPurchase);
        todayEvents.setAdapter(eventsAdapter);
    }

    private void findViews(){
        todayEvents = findViewById(R.id.calendar_RCY_daily_events);

    }

    //region Callbacks
    private View.OnClickListener onClickedPurchase = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //purchase system...
        }
    };

    private View.OnClickListener onClickedAdd = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // load add fragment....
            addEventFragment = new AddEventFragment();
            addEventFragment.setOnEventAddedListener(eventAdded);
            getSupportFragmentManager().beginTransaction().replace(R.id.calendar_LAY_add_event_placeholder, addEventFragment).commit();

        }
    };

    private OnEventAdded eventAdded = new OnEventAdded() {
        @Override
        public void onEventAdded(Event event) {
            Log.i("pttt", event.toString());
            EventDataManager.getInstance().storeEvent(event);
            getSupportFragmentManager().beginTransaction().hide(addEventFragment).commit();
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
}