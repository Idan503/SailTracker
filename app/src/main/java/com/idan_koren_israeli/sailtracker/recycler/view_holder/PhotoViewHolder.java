package com.idan_koren_israeli.sailtracker.recycler.view_holder;

import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.GalleryPhoto;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.recycler.listener.OnPhotoClickedListener;

public class PhotoViewHolder extends RecyclerView.ViewHolder {

    private ImageView innerImage;
    private ImageView loadingImage;
    private RotateAnimation loadingAnimation;
    private GalleryPhoto photo;

    private final static int LOADING_ANIMATION_DURATION = 3000;

    public PhotoViewHolder(@NonNull View itemView) {
        super(itemView);
        findView(itemView);
        initLoadingAnimation();
    }

    private void findView(View parent){
        innerImage = parent.findViewById(R.id.photo_item_IMG_inner_image);
        loadingImage = parent.findViewById(R.id.photo_item_IMG_loading);

    }

    private void initLoadingAnimation(){
        CommonUtils.getInstance().setImageResource(loadingImage, R.drawable.ic_loading);
        RotateAnimation loadingAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        loadingAnimation.setDuration(LOADING_ANIMATION_DURATION);
        loadingAnimation.setRepeatCount(Animation.INFINITE);
        loadingAnimation.setInterpolator(new LinearInterpolator());
        loadingImage.setAnimation(loadingAnimation);
        loadingAnimation.start();
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
