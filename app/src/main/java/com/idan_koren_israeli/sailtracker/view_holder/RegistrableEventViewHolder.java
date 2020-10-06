package com.idan_koren_israeli.sailtracker.view_holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.view_holder.listener.OnEventClickedListener;

/**
 * Event card with an option to register to the event.
 *
 */
public class RegistrableEventViewHolder extends EventViewHolder {
    private MaterialButton registerButton;
    private boolean registered;

    public RegistrableEventViewHolder(@NonNull View itemView) {
        super(itemView);
        registered = false;
        findViews();
    }

    public void setEventContent(Event event){
        super.setEventContent(event);
    }


    public void setButtonListener(final OnEventClickedListener register, final OnEventClickedListener unregister){
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
    }

}
