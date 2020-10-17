package com.idan_koren_israeli.sailtracker.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;

public abstract class BaseActivity extends AppCompatActivity {


    @Override
    protected void onStart() {
        super.onStart();

        // This is called after onCreate, therefor contentview is already set
        setBackgroundImage();
    }

    protected void setBackgroundImage(){
        ImageView backgroundImage = findViewById(R.id.base_IMG_background);
        // Id this id is not found in the content view, backgroundImage will be null.
        if(backgroundImage!=null)
            CommonUtils.getInstance().setImageResource(backgroundImage,R.drawable.img_background);
    }



}
