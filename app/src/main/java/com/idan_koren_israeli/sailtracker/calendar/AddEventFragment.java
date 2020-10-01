package com.idan_koren_israeli.sailtracker.calendar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Minutes;

public class AddEventFragment extends Fragment {

    RadioGroup eventTypeRadio;
    EditText nameEdit, descriptionEdit, maxParticipants, price;
    Button finishButton;

    OnEventAdded eventAdded;


    public AddEventFragment() {
        // Required empty public constructor
    }

    public static AddEventFragment newInstance() {
        AddEventFragment fragment = new AddEventFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent =  inflater.inflate(R.layout.fragment_add_event, container, false);
        findViews(parent);
        setListeners();
        return parent;
    }


    private void findViews(View parent){
        this.nameEdit = parent.findViewById(R.id.add_event_EDT_name);
        this.descriptionEdit = parent.findViewById(R.id.event_item_LBL_description);
        this.eventTypeRadio = parent.findViewById(R.id.add_event_RAT_select_type);
        this.finishButton = parent.findViewById(R.id.login_BTN_next);
        this.maxParticipants = parent.findViewById(R.id.add_event_EDT_max_participants);
        this.price = parent.findViewById(R.id.add_event_EDT_price);
    }

    private void setListeners(){
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventAdded.onEventAdded(generateEvent());
            }
        });
    }

    public Event generateEvent(){
        String name = nameEdit.getText().toString();
        String description = descriptionEdit.getText().toString();
        DateTime start = DateTime.now();
        Minutes length =  Minutes.minutes(90);
        return new Event(name, description, start, length,null);
    }

}