package com.idan_koren_israeli.sailtracker.club;

import java.util.Comparator;

public class SortByStartTime implements Comparator<Event> {
    @Override
    public int compare(Event event1, Event event2) {
        long diff = event1.getStartTime() - event2.getStartTime();
        return (int) diff;
    }
}
