package com.idan_koren_israeli.sailtracker.recycler.view_holder;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.common.SharedPrefsManager;
import com.idan_koren_israeli.sailtracker.firebase.EventDataManager;
import com.idan_koren_israeli.sailtracker.recycler.CalendarRecyclerManager;
import com.idan_koren_israeli.sailtracker.recycler.listener.OnEventClickedListener;

import org.joda.time.DateTime;

/**
 * Event View Holder with extra functionality
 * to register/unregister from events by pressing an additional button
 *
 * Each Registrable card will show information as a regular Event card.
 *
 */
public class RegistrableEventViewHolder extends EventViewHolder {
    private MaterialButton registerButton;
    private boolean registered;
    private boolean watching;
    private boolean deletable;
    private Button deleteButton;

    public RegistrableEventViewHolder(@NonNull View itemView) {
        super(itemView);
        registered = false;
        deletable = false;
        watching = false;
        findViews();
        setDeleteOption();
    }

    public RegistrableEventViewHolder(@NonNull View itemView, boolean deletable) {
        super(itemView);
        registered = false;
        watching = false;
        this.deletable = deletable;
        findViews();
        setDeleteOption();
    }

    private void findViews(){
        registerButton = itemView.findViewById(R.id.event_item_BTN_register);
        deleteButton = itemView.findViewById(R.id.register_event_item_BTN_delete);
    }


    public void setEventContent(Event event){
        super.setEventContent(event);
        ColorStateList disabledBackground = ContextCompat.getColorStateList(registerButton.getContext(), R.color.lighter_grey);
        ColorStateList transparent = ContextCompat.getColorStateList(registerButton.getContext(), android.R.color.transparent);

        if(event.getStartDateTime().getMillis() < DateTime.now().getMillis()){
            // This event is already started, disabling functionality
            registerButton.setOnClickListener(onClickedAfterStarted);


            registerButton.setBackgroundTintList(disabledBackground);

            //Un-clickable look
            registerButton.setStrokeWidth(0);
            registerButton.setRippleColor(transparent);

        }

        if(event.isFull() && !registered){
            registerButton.setBackgroundTintList(disabledBackground);
        }



    }


    //region Register/Unregister Functionality
    public void setButtonListener(final OnEventClickedListener register, final OnEventClickedListener unregister,
                                  final OnEventClickedListener watch, final OnEventClickedListener unwatch){
        if(event.getStartDateTime().getMillis() < DateTime.now().getMillis())
            return; // event already started, button should not be functional

        this.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registered)
                    unregister.onButtonClicked(event);
                else{
                    if(watching) {
                        unwatch.onButtonClicked(event);
                        showWatchButton(event.getPrice());
                        watching = false;
                    }
                    else{
                        if(event.isFull()){
                            watch.onButtonClicked(event);
                            showUnwatchButton(event.getPrice());
                            watching = true;
                        }
                        else
                            register.onButtonClicked(event);
                    }

                }

            }
        });
    }


    public void setIsRegistered(boolean isRegistered){
        this.registered = isRegistered;
        if(event!=null)
            updateRegisterButton();

    }

    private void updateRegisterButton(){
        int eventPrice = event.getPrice();

        if(!registered){
            if(!event.isFull()) {
                showUnregisterButton(eventPrice);
            }
            else{
                // Showing Watch/unwatch card
                SharedPrefsManager sp = SharedPrefsManager.getInstance();
                String memberWatch = sp.getString(SharedPrefsManager.KEYS.WATCHED_EVENT,null);
                if(memberWatch==null){
                    // event is full, show watch button
                    showWatchButton(eventPrice);
                }
                else{
                    if(memberWatch.equals(event.getEid())){
                        // unwatch
                        watching = true;
                        showUnwatchButton(eventPrice);
                    }
                    else {
                        // Can't register, event full, cant watch 2 events
                        showEventFullButton(eventPrice);
                    }
                }
            }
        }
        else{
            showRegisterButton(eventPrice);
        }
    }



    //endregion


    //region Buttons UI Show


    private void showRegisterButton(int price){
        String registerLabel = String.format(registerButton.getResources().getString(R.string.event_unregister_price_label), price);
        registerButton.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary));
        registerButton.setText(registerLabel);
    }

    private void showUnregisterButton(int price){
        String unregisterLabel = String.format(registerButton.getResources().getString(R.string.event_register_price_label), price);
        registerButton.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimaryDark));
        registerButton.setText(unregisterLabel);
    }

    private void showEventFullButton(int price){
        String fullLabel = String.format(registerButton.getResources().getString(R.string.event_full_price_label), price);
        registerButton.setTextColor(ContextCompat.getColor(itemView.getContext(),  R.color.lighter_grey));
        registerButton.setText(fullLabel);
    }

    private void showWatchButton(int price){
        String watchLabel = String.format(registerButton.getResources().getString(R.string.event_watch_price_label), price);
        registerButton.setTextColor(ContextCompat.getColor(itemView.getContext(),  R.color.colorPrimaryDark));
        registerButton.setText(watchLabel);
    }

    private void showUnwatchButton(int price){
        String unwatchLabel = String.format(registerButton.getResources().getString(R.string.event_unwatch_price_label), price);
        registerButton.setTextColor(ContextCompat.getColor(itemView.getContext(),  R.color.colorPrimaryDark));
        registerButton.setText(unwatchLabel);
    }

    //endregion



    //region Delete Event
    private void setDeleteOption() {
        if(deletable)
            deleteButton.setOnClickListener(onDeleteButtonClicked);
        else
            deleteButton.setVisibility(View.GONE);
    }

    private View.OnClickListener onDeleteButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(deletable) {
                MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(view.getContext());
                dialog.setTitle("Delete " + event.getName());
                dialog.setMessage("Are you sure you would like to delete " + event.getName() + "?");
                dialog.setPositiveButton("Delete", onDeleteConfirmed);
                dialog.setNegativeButton("Cancel", null);
                dialog.show();
            }
        }
    };

    private DialogInterface.OnClickListener onDeleteConfirmed = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            CalendarRecyclerManager.getInstance().deleteEventItem(event);
            EventDataManager.getInstance().deleteEvent(event);
        }
    };

    //endregion

    //region Unable to Register Callbacks

    private View.OnClickListener onClickedAfterStarted = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CommonUtils.getInstance().showToast("Event is already started");
        }
    };

    //endregion

}
