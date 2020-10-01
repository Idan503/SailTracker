package com.idan_koren_israeli.sailtracker.calendar;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.common.BaseActivity;

public class CalendarActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        getSupportFragmentManager().beginTransaction().replace(R.id.calendar_LAY_add_event_placeholder, new AddEventFragment()).commit();

    }


    private void findViews(){


    }
}