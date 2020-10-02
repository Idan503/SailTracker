package com.idan_koren_israeli.sailtracker.firebase.callbacks;

import com.idan_koren_israeli.sailtracker.club.Event;

import java.util.ArrayList;

public interface OnEventsLoaded {
    void onEventsListener(ArrayList<Event> eventsLoaded);
}
