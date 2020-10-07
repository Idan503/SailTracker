package com.idan_koren_israeli.sailtracker.view_holder;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.Event;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;

public class AddEventViewHolder extends RecyclerView.ViewHolder {
    private MaterialButton button;

    public AddEventViewHolder(@NonNull View itemView) {
        super(itemView);
        findViews();
    }


    public void setClickListener(View.OnClickListener listener){
        button.setOnClickListener(listener);
        Log.i("pttt", "onClick ste");
    }


    private void findViews(){
        button = itemView.findViewById(R.id.event_add_item_BTN_button);
    }


}
