package com.idan_koren_israeli.sailtracker.gallery;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;

public class PhotoCollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Uri[] photosRefs;
    private LayoutInflater mInflater;

    PhotoCollectionAdapter(Context context, Uri[] data){
        this.mInflater = LayoutInflater.from(context);
        this.photosRefs = data;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the cell layout from xml
        View view = mInflater.inflate(R.layout.photo_collection_item, parent, false);
        return new PhotoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Binds data (ImagePhoto) into the view
        ImageView innerImage = holder.itemView.findViewById(R.id.photo_item_IMG_inner_image);
        CommonUtils.getInstance().setImageResource(innerImage, photosRefs[position]);

    }

    @Override
    public int getItemCount() {
        return photosRefs.length;
    }

    Uri getItem(int id) {
        return photosRefs[id];
    }

}
