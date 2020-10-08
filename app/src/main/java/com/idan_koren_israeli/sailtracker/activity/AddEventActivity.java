package com.idan_koren_israeli.sailtracker.activity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewFlipper;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.api.Distribution;
import com.google.type.TimeOfDay;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.enums.EventType;
import com.idan_koren_israeli.sailtracker.club.exception.AddedEventInputMismatch;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
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
    private EditText nameEdit, descriptionEdit;
    private MaterialButton maxParticipantsButton, priceButton;
    private ViewFlipper viewFlipper;
    private MaterialButton nextButton, backButton;
    private MaterialButton startTimeButton, endTimeButton;

    private TimeOfDay startTime = null, endTime = null;

    private Context mContext = this;

    private LocalDate date;

    public interface KEYS{
        String ADDED_EVENT = "addedEvent";
        String EVENT_DATE = "eventDate";
    }


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
        maxParticipantsButton = findViewById(R.id.add_event_BTN_max_participants);
        priceButton = findViewById(R.id.add_event_BTN_price);
        backButton = findViewById(R.id.add_event_BTN_back);
        nextButton = findViewById(R.id.add_event_BTN_next);
        startTimeButton = findViewById(R.id.add_event_BTN_start_time);
        endTimeButton = findViewById(R.id.add_event_BTN_end_time);
        viewFlipper = findViewById(R.id.add_event_LAY_flipper);
    }

    private void setListeners(){
        eventTypeRadio.setOnCheckedChangeListener(onRadioButtonChanged);

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

        priceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPricePicker();
            }
        });

        maxParticipantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMaxParticipantsPicker();
            }
        });

    }

    private RadioGroup.OnCheckedChangeListener onRadioButtonChanged = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int id) {
            for(int i=0;i<radioGroup.getChildCount();i++){
                MaterialRadioButton child = (MaterialRadioButton) radioGroup.getChildAt(i);
                if(child.getId()==id)
                    child.setAlpha(1f);
                else
                    child.setAlpha(0.35f);
            }
        }
    };

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
                    viewFlipper.setDisplayedChild(exception.getMenuId());
                }

            } else {
                viewFlipper.setInAnimation(view.getContext(), R.anim.slide_in_right);
                viewFlipper.setOutAnimation(view.getContext(), R.anim.slide_out_left);
                viewFlipper.showNext();
            }
        }
    };

    //region Number Pickers (Participants Limit & Price)

    private void showMaxParticipantsPicker(){
        final Dialog dialog = new Dialog(AddEventActivity.this);
        String title = "Select Participants Limit";
        dialog.setTitle(title);
        dialog.setContentView(R.layout.dialog_numpicker);
        // Finding inside dialog views
        TextView titleText = dialog.findViewById(R.id.numpick_LBL_title);
        MaterialButton setBtn = (MaterialButton) dialog.findViewById(R.id.numpick_BTN_set);
        MaterialButton cancelBtn = (MaterialButton) dialog.findViewById(R.id.numpick_BTN_cancel);
        final NumberPicker picker = (NumberPicker) dialog.findViewById(R.id.numpick_PICK_picker);

        titleText.setText(title);
        // Setting picker values
        final String[] pickerValues = new String[50 + 1];
        pickerValues[0] = "Unlimited";
        for(int i=1;i<=50;i++){
            pickerValues[i] = String.valueOf(i);
        }

        picker.setWrapSelectorWheel(false);
        picker.setDisplayedValues(pickerValues);
        picker.setMinValue(0);
        picker.setMaxValue(pickerValues.length-1);
        picker.setValue(1);
        // Setting buttons listeners that effects the inner dialog created
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maxParticipantsButton.setText(pickerValues[picker.getValue()]);
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }



    private void showPricePicker(){

        final Dialog dialog = new Dialog(AddEventActivity.this);
        String title = "Select Event Price";
        dialog.setTitle(title);
        dialog.setContentView(R.layout.dialog_numpicker);
        // Finding inside dialog views
        TextView titleText = dialog.findViewById(R.id.numpick_LBL_title);
        MaterialButton setBtn = dialog.findViewById(R.id.numpick_BTN_set);
        MaterialButton cancelBtn = dialog.findViewById(R.id.numpick_BTN_cancel);
        final NumberPicker picker = dialog.findViewById(R.id.numpick_PICK_picker);

        titleText.setText(title);
        // Setting picker values
        final String[] pickerValues = new String[10 + 1];
        pickerValues[0] = "Free";
        for(int i=1;i<=10;i++){
            pickerValues[i] = String.valueOf(i);
        }

        picker.setWrapSelectorWheel(false);
        picker.setDisplayedValues(pickerValues);
        picker.setMinValue(0);
        picker.setMaxValue(pickerValues.length-1);
        picker.setValue(1);
        // Setting buttons listeners that effects the inner dialog created
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceButton.setText(pickerValues[picker.getValue()]);
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }



    //endregion


    //region Event Generation
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

        if(priceButton.getText().toString().matches(""))
            throw new AddedEventInputMismatch("Please enter price", 2);

        if(maxParticipantsButton.getText().toString().matches(""))
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


        String priceString = priceButton.getText().toString();
        String maxMemberCountString = maxParticipantsButton.getText().toString();
        int price, maxMemberCount; // final results

        if(priceString.equalsIgnoreCase("Free"))
            price = 0; //Free
        else
            price = Integer.parseInt(priceString);

        if(maxMemberCountString.equalsIgnoreCase("Unlimited"))
            maxMemberCount = -1;
        else
            maxMemberCount = Integer.parseInt(maxMemberCountString);


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

    //endregion


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