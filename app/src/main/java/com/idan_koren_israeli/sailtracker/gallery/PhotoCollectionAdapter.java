package com.idan_koren_israeli.sailtracker.gallery;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;

public class PhotoCollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private GalleryPhoto[] photos;
    private LayoutInflater mInflater;
    private View.OnClickListener photoClickListener;
    private static final int NUM_OF_COLUMNS = 3;

    PhotoCollectionAdapter(Context context, GalleryPhoto[] photos){
        this.mInflater = LayoutInflater.from(context);
        this.photos = photos;
    }

    public void setPhotoClickListener(View.OnClickListener listener){
        this.photoClickListener = listener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the cell layout from xml
        View itemView = mInflater.inflate(R.layout.photo_collection_item, parent, false);
        int height = parent.getMeasuredHeight() / NUM_OF_COLUMNS;
        itemView.setMinimumHeight(height);
        PhotoViewHolder photoHolder = new PhotoViewHolder(itemView);
        photoHolder.setOnClickListener(photoClickListener);
        return photoHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Binds data (ImagePhoto) into the view using the given uri
        ImageView innerImage = holder.itemView.findViewById(R.id.photo_item_IMG_inner_image);
        CommonUtils.getInstance().setImageResource(innerImage, photos[position].getUri());

    }

    @Override
    public int getItemCount() {
        return photos.length;
    }

    GalleryPhoto getItem(int id) {
        return photos[id];
    }


}
