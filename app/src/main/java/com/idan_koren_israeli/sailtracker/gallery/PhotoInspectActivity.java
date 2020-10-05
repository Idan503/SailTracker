package com.idan_koren_israeli.sailtracker.gallery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.common.LoadingFragment;


/**
 *
 * This activity lets the user view a certain photo in a "full screen" mode.
 * And zoom in and translate via touch gestures
 *
 */
public class PhotoInspectActivity extends AppCompatActivity {

    private LoadingFragment loadingFragment;
    private ImageView imageView;
    private String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_inspect);
        findViews();
        loadPhotoUrl();

        loadingFragment.show();
        loadPhoto();

    }

    private void findViews(){
        imageView = findViewById(R.id.photo_inspect_IMG_photo);
        loadingFragment = (LoadingFragment) getSupportFragmentManager().findFragmentById(R.id.photo_inspect_FRAG_loading);
    }

    private void loadPhotoUrl(){
        this.photoUrl = getIntent().getStringExtra(PhotoCollectionFragment.KEYS.PHOTO_URL);
    }

    private void loadPhoto(){
        CommonUtils.getInstance().setImageResource(imageView, Uri.parse(photoUrl),imageLoadListener);
    }

    private RequestListener<Drawable> imageLoadListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            loadingFragment.hide();
            return false;
        }
    };
}