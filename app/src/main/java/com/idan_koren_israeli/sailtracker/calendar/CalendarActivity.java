package com.idan_koren_israeli.sailtracker.calendar;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.common.BaseActivity;
import com.idan_koren_israeli.sailtracker.firebase.MembersDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnFileCheckFinishListener;

public class CalendarActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        getSupportFragmentManager().beginTransaction().replace(R.id.calendar_LAY_add_event_placeholder, new AddEventFragment()).commit();

        MembersDataManager.getInstance().isManagerMember(isMember);


    }

    OnFileCheckFinishListener isMember = new OnFileCheckFinishListener() {
        @Override
        public void onCheckFinished(boolean result) {
            System.out.println(result);
        }
    };


    private void findViews(){


    }
}