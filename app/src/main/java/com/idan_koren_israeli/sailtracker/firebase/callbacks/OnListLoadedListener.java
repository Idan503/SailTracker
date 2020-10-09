package com.idan_koren_israeli.sailtracker.firebase.callbacks;

import java.util.ArrayList;
import java.util.List;

public interface OnListLoadedListener<T> {
    void onListLoaded(List<T> list);
}
