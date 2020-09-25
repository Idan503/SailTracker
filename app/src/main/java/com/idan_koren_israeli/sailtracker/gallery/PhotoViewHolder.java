package com.idan_koren_israeli.sailtracker.gallery;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoViewHolder extends RecyclerView.ViewHolder {
    public PhotoViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(onClickPhoto);
    }

    private View.OnClickListener onClickPhoto = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("pttt", "Photo " + view.getId() + " clicked");
        }
    };
}
