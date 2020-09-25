package com.idan_koren_israeli.sailtracker.gallery;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoViewHolder extends RecyclerView.ViewHolder {
    public PhotoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.itemView.setOnClickListener(listener);
    }


}
