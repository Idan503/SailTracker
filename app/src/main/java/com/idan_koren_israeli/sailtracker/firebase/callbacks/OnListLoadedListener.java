package com.idan_koren_israeli.sailtracker.firebase.callbacks;

import java.util.ArrayList;

public interface OnListLoadedListener<T> {
    void onListLoaded(ArrayList<T> list);
}
