package com.idan_koren_israeli.sailtracker.recycler;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.activity.AddEventActivity;
import com.idan_koren_israeli.sailtracker.activity.CalendarActivity;
import com.idan_koren_israeli.sailtracker.adapter.EventRecyclerAdapter;
import com.idan_koren_israeli.sailtracker.adapter.ManagerEventRecyclerAdapter;
import com.idan_koren_israeli.sailtracker.adapter.RegistrableEventRecyclerAdapter;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.exception.AlreadyRegisteredException;
import com.idan_koren_israeli.sailtracker.club.exception.EventFullException;
import com.idan_koren_israeli.sailtracker.club.exception.NotEnoughPointsException;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.firebase.EventDataManager;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.notification.EventWatchManager;
import com.idan_koren_israeli.sailtracker.recycler.listener.OnEventClickedListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This class will handle everything related to the events adapter that is shown in calendar view
 * Helping management of calendar activity only
 */
public class CalendarRecyclerManager {

    private static CalendarRecyclerManager single_instance = null;

    private CalendarActivity activity;
    private EventRecyclerAdapter adapter;

    private ClubMember currentMember;
    private EventWatchManager watchManager;

    private EventRecyclerType type = EventRecyclerType.REGISTER;

    private CalendarRecyclerManager(CalendarActivity caller)
    {
        this.activity = caller;
        this.watchManager = activity.getWatchManager();
        this.currentMember = MemberDataManager.getInstance().getCurrentMember();
    }

    public static CalendarRecyclerManager getInstance()
    {
        return single_instance;
    }

    public static CalendarRecyclerManager initHelper(CalendarActivity caller)
    {
        single_instance = new CalendarRecyclerManager(caller);
        return single_instance;
    }


    // Changes data on recycler and notifies the adapter
    public void updateEventsList(boolean isMemberManager, ArrayList<Event> allEvents, List<Event> registeredEvents){
        //Log.i("pttt", "UPDATE: allEvents:" + allEvents.size() + " | Register : " + registeredEvents.size());
        if(adapter==null)
        {
            adapter = new RegistrableEventRecyclerAdapter(activity,allEvents, registeredEvents);
            // Default adapter as start
        }

        if(viewingPast())
        {
            if(type!=EventRecyclerType.DEFAULT) {
                // User changed from future view to past
                adapter = initPastViewAdapter(allEvents);
                type = EventRecyclerType.DEFAULT; //to make a NotifyDataChange next time
            }
            else
            {
                //No need to re-init adapter
                Log.i("pttt", allEvents.size() + "Past Notify");
                adapter.setEventsData(allEvents);
                adapter.notifyDataSetChanged();
            }
        }
        else {
            // user watching future/today event - load register/manager adapter (same logic as above)
            if (isMemberManager) {
                if(type!=EventRecyclerType.MANAGER)
                {
                    adapter = initManagerViewAdapter(allEvents,registeredEvents);
                    type = EventRecyclerType.MANAGER;
                }
                else
                {
                    adapter.setEventsData(allEvents);
                    ((RegistrableEventRecyclerAdapter)adapter).setRegisteredEvents(registeredEvents);
                    adapter.notifyDataSetChanged();

                }
            }
            else{
                //Regular register view (same logic as above)
                if(type!=EventRecyclerType.REGISTER)
                {
                    adapter = initRegisterViewAdapter(allEvents,registeredEvents);
                    type = EventRecyclerType.REGISTER;

                }
                else
                {
                    adapter.setEventsData(allEvents);
                    ((RegistrableEventRecyclerAdapter)adapter).setRegisteredEvents(registeredEvents);
                    adapter.notifyDataSetChanged();
                }
            }
        }
        activity.setRecyclerAdapter(adapter);
    }

    //region Creation of Adapter Types
    private EventRecyclerAdapter initPastViewAdapter(List<Event> events)
    {
        Log.i("pttt", events.size() + "Past Init");
        adapter = new EventRecyclerAdapter(activity, events,false);
        return adapter;
    }

    private RegistrableEventRecyclerAdapter initRegisterViewAdapter(List<Event> events, List<Event> alreadyRegistered)
    {
        //Log.i("pttt", events.size() + "Register Init");

        RegistrableEventRecyclerAdapter adapter = new RegistrableEventRecyclerAdapter(activity, events,alreadyRegistered);
        adapter
                .setButtonsListeners(onRegisterClicked, onUnregisterClicked,
                        onWatchClicked, onUnwatchClicked);

        return adapter;
    }

    private ManagerEventRecyclerAdapter initManagerViewAdapter(List<Event> events, List<Event> alreadyRegistered)
    {
        //Log.i("pttt", events.size() + "Manager Init");
        ManagerEventRecyclerAdapter adapter =  new ManagerEventRecyclerAdapter(activity, events,alreadyRegistered);
        adapter.setButtonsListeners(onRegisterClicked, onUnregisterClicked,
                onWatchClicked, onUnwatchClicked);

        adapter.setAddClickedListener(onClickedAddButton);

        return adapter;
    }

    //endregion


    //region RecyclerView's UI Related Callbacks
    private OnEventClickedListener onRegisterClicked = new OnEventClickedListener() {
        @Override
        public void onButtonClicked(Event eventClicked) {
            try {
                if(canMemberRegister(eventClicked)) {
                    EventDataManager.getInstance().registerMember(currentMember, eventClicked);
                    activity.reloadPointsStatus();
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
            activity.reloadPointsStatus();
            CommonUtils.getInstance().showToast("Unregistered successfully!");
        }
    };

    private OnEventClickedListener onWatchClicked = new OnEventClickedListener() {
        @Override
        public void onButtonClicked(Event eventClicked) {
            watchManager.startWatch(eventClicked);

        }
    };

    private OnEventClickedListener onUnwatchClicked = new OnEventClickedListener() {
        @Override
        public void onButtonClicked(Event eventClicked) {
            watchManager.stopWatch(eventClicked);
        }
    };


    private View.OnClickListener onClickedAddButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(activity, AddEventActivity.class);
            intent.putExtra(AddEventActivity.KEYS.EVENT_DATE, activity.getSelectedDate());
            activity.startActivity(intent);
            activity.finish();
        }
    };


    //endregion


    // Members can't register to 2 events that has a common shared time
    // Checking if a given event can be registered by checking time overlap with the others
    private boolean canMemberRegister(Event registerEvent){
        for(Event event : activity.getEventsToShow()){
            if(registerEvent!=event && event.getRegisteredMembers().contains(currentMember.getUid())){
                if(registerEvent.getStartTime() < event.getEndTime()  && event.getStartTime() < registerEvent.getEndTime()){
                    return false; // member is already register to an overlapping event
                }
            }
        }
        return true;
    }


    private boolean viewingPast()
    {
        final long millisOfOneDay = 86400000;
        return activity.getSelectedDate().toDateTimeAtStartOfDay().getMillis() + millisOfOneDay< DateTime.now().getMillis();
    }


}
