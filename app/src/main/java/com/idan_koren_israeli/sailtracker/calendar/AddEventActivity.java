package com.idan_koren_israeli.sailtracker.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.ViewFlipper;

import com.google.android.material.button.MaterialButton;
import com.google.type.TimeOfDay;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.EventType;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.InputMismatchException;
import java.util.UUID;

public class AddEventActivity extends AppCompatActivity {

    private RadioGroup eventTypeRadio;
    private EditText nameEdit, descriptionEdit, maxParticipantsEdit, priceEdit;
    private ViewFlipper viewFlipper;
    private MaterialButton nextButton, backButton;
    private TimePicker startPick, endPick;

    private LocalDate date;

    public interface KEYS{
        String ADDED_EVENT = "addedEvent";
        String ADDED_SAIL = "addedSail";
        String EVENT_DATE = "eventDate";
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        date = (LocalDate) getIntent().getSerializableExtra(KEYS.EVENT_DATE);

        findViews();
        setListeners();
    }



    private void findViews(){
        nameEdit = findViewById(R.id.add_event_EDT_name);
        descriptionEdit = findViewById(R.id.add_event_EDT_description);
        eventTypeRadio = findViewById(R.id.add_event_RAT_select_type);
        maxParticipantsEdit = findViewById(R.id.add_event_EDT_max_participants);
        priceEdit = findViewById(R.id.add_event_EDT_price);
        backButton = findViewById(R.id.add_event_BTN_back);
        nextButton = findViewById(R.id.add_event_BTN_next);
        startPick = findViewById(R.id.add_event_BTN_start_time);
        endPick = findViewById(R.id.add_event_BTN_end_time);
        viewFlipper = findViewById(R.id.add_event_LAY_flipper);
    }

    private void setListeners(){
        nextButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(viewFlipper.getDisplayedChild()==2) {
                            // user is in the last screen, event can be generated
                            // this should be finished
                            Intent intent = new Intent(AddEventActivity.this, CalendarActivity.class);
                            intent.putExtra(KEYS.ADDED_EVENT, generateEvent());
                            startActivity(intent);
                            finish();
                        }
                        else {
                            viewFlipper.setInAnimation(view.getContext(), R.anim.slide_in_right);
                            viewFlipper.setOutAnimation(view.getContext(), R.anim.slide_out_left);
                            viewFlipper.showNext();
                        }
                    }
                });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setInAnimation(view.getContext(), android.R.anim.slide_in_left);
                viewFlipper.setOutAnimation(view.getContext(), android.R.anim.slide_out_right);
                viewFlipper.showPrevious();
            }
        });
    }

    //Creating the event based on the user input
    public Event generateEvent() throws InputMismatchException {
        String name = nameEdit.getText().toString();
        String description = descriptionEdit.getText().toString();


        TimeOfDay startTimeOfDay = TimeOfDay.newBuilder()
                .setHours(startPick.getHour())
                .setMinutes(startPick.getMinute())
                .build();

        TimeOfDay endTimeOfDay = TimeOfDay.newBuilder()
                .setHours(endPick.getHour())
                .setMinutes(endPick.getMinute())
                .build();

        DateTime startTime = date.toDateTimeAtStartOfDay().plusHours(startPick.getHour()).plusMinutes(startPick.getMinute());
        int lengthMinutes = calculateMinutesBetween(startTimeOfDay, endTimeOfDay);

        int price = Integer.parseInt(priceEdit.getText().toString());
        int maxMemberCount = Integer.parseInt(maxParticipantsEdit.getText().toString());

        EventType type = EventType.FREE_EVENT;
        switch (eventTypeRadio.getCheckedRadioButtonId()){
            case R.id.add_event_RBTN_free_event:
                type = EventType.FREE_EVENT;
                break;
            case R.id.add_event_RBTN_members_sail:
                type = EventType.MEMBERS_SAIL;
                break;
            case R.id.add_event_event_RBTN_guided_sail:
                type = EventType.GUIDED_SAIL;
                break;

        }

        return new Event(name, description, type,startTime.getMillis(), lengthMinutes, price, maxMemberCount);
    }

    // For calculating length of event based on start and end day times
    private int calculateMinutesBetween(TimeOfDay a, TimeOfDay b){
        return ((b.getHours() - a.getHours()) * 60) + (b.getMinutes() - a.getMinutes());
    }

}