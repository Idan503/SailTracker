package com.idan_koren_israeli.sailtracker.view_holder;

import android.app.Activity;
import android.app.DownloadManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.activity.BaseActivity;
import com.idan_koren_israeli.sailtracker.club.GalleryPhoto;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.fragment.LoadingFragment;
import com.idan_koren_israeli.sailtracker.view_holder.listener.OnPhotoClickedListener;

public class PhotoViewHolder extends RecyclerView.ViewHolder {

    private ImageView innerImage;
    private GalleryPhoto photo;

    public PhotoViewHolder(@NonNull View itemView) {
        super(itemView);
        findView(itemView);
    }

    private void findView(View parent){
        innerImage = parent.findViewById(R.id.photo_item_IMG_inner_image);

    }

    public void setOnClickListener(final OnPhotoClickedListener listener){
        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPhotoClicked(photo);
            }
        });
    }

    public void setPhoto(GalleryPhoto photo){
        this.photo = photo;
        CommonUtils.getInstance().setImageResource(innerImage, Uri.parse(photo.getUrl()));
    }




}
