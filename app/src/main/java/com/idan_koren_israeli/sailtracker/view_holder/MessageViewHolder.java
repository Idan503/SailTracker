package com.idan_koren_israeli.sailtracker.view_holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.idan_koren_israeli.sailtracker.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    private TextView textView;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        findViews();
    }

    public void setTextGravity(int gravity){
        if(textView!=null)
            textView.setGravity(gravity);
    }

    public void setText(CharSequence text){
        if(textView!=null)
            textView.setText(text);
    }


    private void findViews(){
        textView = itemView.findViewById(R.id.message_item_LBL_text);
    }


}
