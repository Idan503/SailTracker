package com.idan_koren_israeli.sailtracker.activity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.ViewFlipper;

import com.google.android.material.button.MaterialButton;
import com.google.type.TimeOfDay;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.enums.EventType;
import com.idan_koren_israeli.sailtracker.club.exception.AddedEventInputMismatch;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.InputMismatchException;

/**
 *
 * This activity will be shown only to managers of the clubs
 * Which are pressing on "Add Event" button in the calendar activity.
 *
 * This menu is in whole different activity to improve UX
 *
 */
public class AddEventActivity extends BaseActivity {

    private RadioGroup eventTypeRadio;
    private EditText nameEdit, descriptionEdit, maxParticipantsEdit, priceEdit;
    private ViewFlipper viewFlipper;
    private MaterialButton nextButton, backButton;
    private MaterialButton startTimeButton, endTimeButton;

    private TimeOfDay startTime = null, endTime = null;

    private Context mContext = this;

    private LocalDate date;

    public interface KEYS{
        String ADDED_EVENT = "addedEvent";
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
        startTimeButton = findViewById(R.id.add_event_BTN_start_time);
        endTimeButton = findViewById(R.id.add_event_BTN_end_time);
        viewFlipper = findViewById(R.id.add_event_LAY_flipper);
    }

    private void setListeners(){

        // Setting dialog time

        nextButton.setOnClickListener(loadNextMenu);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewFlipper.getDisplayedChild()==0){
                    Intent intent = new Intent(AddEventActivity.this, CalendarActivity.class);
                    startActivity(intent);
                    finish(); // nothing to be back to
                }
                else {
                    viewFlipper.setInAnimation(view.getContext(), android.R.anim.slide_in_left);
                    viewFlipper.setOutAnimation(view.getContext(), android.R.anim.slide_out_right);
                    viewFlipper.showPrevious();
                }
            }
        });

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog picker =
                        new TimePickerDialog(mContext,onStartTimePicked,12,0,true);

                picker.show();
            }
        });

        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog picker =
                        new TimePickerDialog(mContext,onEndTimePicked,16,0,true);

                picker.show();
            }
        });

    }

    private View.OnClickListener loadNextMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (viewFlipper.getDisplayedChild() == 2) {
                // user is in the last menu screen
                try {
                    validateInput();
                    // Code gets to here iff all input is valid, new event can be created
                    Intent intent = new Intent(AddEventActivity.this, CalendarActivity.class);
                    intent.putExtra(KEYS.ADDED_EVENT, generateEvent());
                    startActivity(intent);
                    finish();
                } catch (AddedEventInputMismatch exception) {
                    CommonUtils.getInstance().showToast(exception.getMessage());
                    Log.i("pttt", "child: " + exception.getMenuId());
                    viewFlipper.setDisplayedChild(exception.getMenuId());
                }

            } else {
                viewFlipper.setInAnimation(view.getContext(), R.anim.slide_in_right);
                viewFlipper.setOutAnimation(view.getContext(), R.anim.slide_out_left);
                viewFlipper.showNext();
            }
        }
    };

    // Checks that all input is valid and an event can be generated
    private void validateInput() throws AddedEventInputMismatch{

        if(eventTypeRadio.getCheckedRadioButtonId() == -1) //No button checked
            throw new AddedEventInputMismatch("Please select event type", 0);

        if(nameEdit.getText().toString().matches(""))
            throw new AddedEventInputMismatch("Please enter event name", 0);

        if(startTime==null)
            throw new AddedEventInputMismatch("Please enter start time", 1);

        if(endTime==null)
            throw new AddedEventInputMismatch("Please enter end time", 1);

        if(calculateMinutesBetween(startTime,endTime) <= 0)
            throw new AddedEventInputMismatch("End time should be after start time", 1);

        if(maxParticipantsEdit.getText().toString().matches(""))
            throw new AddedEventInputMismatch("Please enter price", 2);

        if(maxParticipantsEdit.getText().toString().matches(""))
            throw new AddedEventInputMismatch("Please enter price", 2);

        if(maxParticipantsEdit.getText().toString().matches(""))
            throw new AddedEventInputMismatch("Please enter max. participants", 2);

        if(descriptionEdit.getText().toString().matches(""))
            throw new AddedEventInputMismatch("Please enter event description", 2);

    }



    //Creating the event based on the user input
    public Event generateEvent() throws InputMismatchException {
        String name = nameEdit.getText().toString();
        String description = descriptionEdit.getText().toString();


        DateTime startDateTime = date.toDateTimeAtStartOfDay().plusHours(startTime.getHours()).plusMinutes(startTime.getMinutes());
        int lengthMinutes = calculateMinutesBetween(startTime, endTime);


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

        return new Event(name, description, type,startDateTime.getMillis(), lengthMinutes, price, maxMemberCount);
    }

    // For calculating length of event based on start and end day times
    private int calculateMinutesBetween(TimeOfDay a, TimeOfDay b){
        return ((b.getHours() - a.getHours()) * 60) + (b.getMinutes() - a.getMinutes());
    }


    //region Time Selection Callbacks
    private TimePickerDialog.OnTimeSetListener onStartTimePicked = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            startTime = TimeOfDay.newBuilder().setHours(i).setMinutes(i1).build();
            String suffix = (startTime.getHours()<12)?"AM":"PM";
            startTimeButton.setText(getResources().getString(R.string.hour_time_format,i,i1,suffix));
        }
    };

    private TimePickerDialog.OnTimeSetListener onEndTimePicked = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            endTime = TimeOfDay.newBuilder().setHours(i).setMinutes(i1).build();
            String suffix = (endTime.getHours()<12)?"AM":"PM";
            endTimeButton.setText(getResources().getString(R.string.hour_time_format,i,i1,suffix));
        }
    };

    //endregion


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backButton.performClick();
    }
}