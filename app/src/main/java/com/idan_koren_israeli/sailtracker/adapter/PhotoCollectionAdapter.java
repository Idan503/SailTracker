package com.idan_koren_israeli.sailtracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.club.GalleryPhoto;
import com.idan_koren_israeli.sailtracker.club.comparator.SortByCreationTime;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.recycler.listener.OnPhotoClickedListener;
import com.idan_koren_israeli.sailtracker.recycler.view_holder.PhotoViewHolder;

import java.util.ArrayList;


public class PhotoCollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<GalleryPhoto> photos;
    private LayoutInflater mInflater;
    private OnPhotoClickedListener photoClickListener;
    private static final int NUM_OF_COLUMNS = 3;

    public PhotoCollectionAdapter(Context context){
        this.mInflater = LayoutInflater.from(context);
        this.photos = new ArrayList<>();
    }

    public PhotoCollectionAdapter(Context context, ArrayList<GalleryPhoto> photos){
        this.mInflater = LayoutInflater.from(context);
        if(photos==null)
            this.photos = new ArrayList<>();
        else
            this.photos = photos;
    }

    public void setPhotoClickListener(OnPhotoClickedListener listener){
        this.photoClickListener = listener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the cell layout from xml
        View itemView = mInflater.inflate(R.layout.recycler_photo_item, parent, false);
        int height = parent.getMeasuredHeight() / NUM_OF_COLUMNS;
        itemView.setMinimumHeight(height);
        PhotoViewHolder photoHolder = new PhotoViewHolder(itemView);
        photoHolder.setOnClickListener(photoClickListener);
        return photoHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((PhotoViewHolder) holder).setPhoto(photos.get(position));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    GalleryPhoto getItem(int position) {
        return photos.get(position);
    }


    public void setMemberPhotos(ClubMember member){
        photos.clear();
        photos.addAll(member.getGalleryPhotos());
        photos.sort(new SortByCreationTime());
        notifyDataSetChanged();
    }

    public void clear(){
        photos.clear();
        notifyDataSetChanged();
    }
}
