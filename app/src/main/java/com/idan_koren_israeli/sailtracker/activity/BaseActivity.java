package com.idan_koren_israeli.sailtracker.activity;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // This is called after onCreate, therefor contentview is already set
        setBackgroundImage();
    }

    protected void setBackgroundImage(){
        ImageView backgroundImage = findViewById(R.id.base_IMG_background);
        if(backgroundImage!=null)
            CommonUtils.getInstance().setImageResource(backgroundImage,R.drawable.img_background);
    }

}
