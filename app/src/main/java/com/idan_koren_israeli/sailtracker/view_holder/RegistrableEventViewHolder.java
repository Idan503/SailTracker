package com.idan_koren_israeli.sailtracker.view_holder;

import android.animation.LayoutTransition;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.firebase.EventDataManager;
import com.idan_koren_israeli.sailtracker.view_holder.listener.OnEventClickedListener;

/**
 * Event card with an option to register to the event.
 * Extends the regular card event which doesn't have the register button
 *
 */
public class RegistrableEventViewHolder extends EventViewHolder {
    private MaterialButton registerButton;
    private boolean registered;
    private boolean deletable;
    private Button deleteButton;

    public RegistrableEventViewHolder(@NonNull View itemView) {
        super(itemView);
        registered = false;
        deletable = false;
        findViews();
        setDeleteOption();
    }

    public RegistrableEventViewHolder(@NonNull View itemView, boolean deletable) {
        super(itemView);
        registered = false;
        this.deletable = deletable;
        findViews();
        setDeleteOption();
    }

    private void setDeleteOption() {
        if(deletable)
            deleteButton.setOnClickListener(onDeleteButtonClicked);
        else
            deleteButton.setVisibility(View.GONE);
    }

    public void setEventContent(Event event){
        super.setEventContent(event);
    }


    public void setRegisterButtonListener(final OnEventClickedListener register, final OnEventClickedListener unregister){
        this.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registered)
                    unregister.onButtonClicked(event);
                else
                    register.onButtonClicked(event);
            }
        });
    }


    public void setIsRegistered(boolean isRegistered){
        this.registered = isRegistered;
        // change the button view to something else
        registerButton.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimaryDark));

    }

    private void findViews(){
        registerButton = itemView.findViewById(R.id.event_item_BTN_register);
        deleteButton = itemView.findViewById(R.id.register_event_item_BTN_delete);
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
            EventDataManager.getInstance().deleteEvent(event);
        }
    };

}
