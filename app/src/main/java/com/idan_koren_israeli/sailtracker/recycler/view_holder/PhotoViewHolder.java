package com.idan_koren_israeli.sailtracker.recycler.view_holder;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.GalleryPhoto;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.recycler.listener.OnPhotoClickedListener;

public class PhotoViewHolder extends RecyclerView.ViewHolder {

    private ImageView innerImage;
    private ImageView loadingImage;
    private GalleryPhoto photo;

    public PhotoViewHolder(@NonNull View itemView) {
        super(itemView);
        findView(itemView);
        loadingImage.setVisibility(View.VISIBLE); // start as loading
    }

    private void findView(View parent){
        innerImage = parent.findViewById(R.id.photo_item_IMG_inner_image);
        loadingImage = parent.findViewById(R.id.photo_item_IMG_loading);
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
        CommonUtils.getInstance().setImageResource(innerImage, Uri.parse(photo.getUrl()), new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                if(e!=null)
                    Log.e(getClass().getSimpleName(), e.getMessage());
                CommonUtils.getInstance().showToast("Problem occurred loading gallery photo");
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                loadingImage.setVisibility(View.GONE);
                return false;
            }
        });
    }



}
