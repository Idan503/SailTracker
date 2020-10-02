package com.idan_koren_israeli.sailtracker.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ViewFlipper;

import com.google.android.material.button.MaterialButton;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.club.Sail;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.UUID;

public class AddEventActivity extends AppCompatActivity {

    private RadioGroup eventTypeRadio;
    private EditText nameEdit, descriptionEdit, maxParticipants, price;
    private ViewFlipper viewFlipper;
    private MaterialButton nextButton, backButton;

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
        maxParticipants = findViewById(R.id.add_event_EDT_max_participants);
        price = findViewById(R.id.add_event_EDT_price);
        backButton = findViewById(R.id.add_event_BTN_back);
        nextButton = findViewById(R.id.add_event_BTN_next);
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
                            viewFlipper.setInAnimation(view.getContext(), android.R.anim.slide_in_left);
                            viewFlipper.setOutAnimation(view.getContext(), android.R.anim.slide_out_right);
                            viewFlipper.showNext();
                        }
                    }
                });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setInAnimation(view.getContext(), R.anim.slide_in_right);
                viewFlipper.setOutAnimation(view.getContext(), R.anim.slide_out_left);
                viewFlipper.showPrevious();
            }
        });
    }

    public Event generateEvent(){
        String name = nameEdit.getText().toString();
        String description = descriptionEdit.getText().toString();
        DateTime start = date.toDateTimeAtStartOfDay().plusHours(12).plusMinutes(15);
        return new Event(UUID.randomUUID().toString(),name, description, start.getMillis(), 90,null);
    }

    public Sail generateSail(){
        return new Sail(generateEvent(),
                Integer.parseInt(price.getText().toString()),
                Integer.parseInt(maxParticipants.getText().toString()));
    }
}