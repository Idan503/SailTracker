package com.idan_koren_israeli.sailtracker.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.custom_views.SquareImageView;


public class LoadingFragment extends Fragment {

    private static final String DEFAULT_MESSAGE = "Loading...";

    private View parent;

    private SquareImageView imageView;
    private TextView textView;
    private String message;
    private boolean showText;
    private boolean startHidden;


    public LoadingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onInflate(@NonNull Context context,@NonNull AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        TypedArray type = context.obtainStyledAttributes(attrs,R.styleable.ProfileFragment);


        //Retrieve attributes from xml
        showText = type.getBoolean(R.styleable.LoadingFragment_show_text,true);
        message = type.getString(R.styleable.LoadingFragment_text);
        startHidden = type.getBoolean(R.styleable.LoadingFragment_starts_hidden, true);

        if(message == null || message.equals(""))
            message = DEFAULT_MESSAGE;

        Log.i("pttt", "Loading Inflating");

        type.recycle();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parent = inflater.inflate(R.layout.fragment_loading, container, false);
        findViews(parent);

        applyAttributes();
        rotateImage();
        return parent;
    }

    private void findViews(View parent){
        textView = parent.findViewById(R.id.loading_LBL_text);
        imageView = parent.findViewById(R.id.loading_IMG_image);
    }


    private void applyAttributes(){
        if(startHidden){
            hide();
        }

        if(!showText)
            textView.setVisibility(View.GONE);
        else
            textView.setText(message);

    }

    public void show(){
        parent.setVisibility(View.VISIBLE);
    }

    public void hide(){
        parent.setVisibility(View.GONE);
    }

    public void setMessage(String message){
        textView.setText(message);
    }

    // Animation rotation for the loading view
    private void rotateImage(){
        RotateAnimation rotation = new RotateAnimation(0,360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotation.setDuration(2000);
        rotation.setRepeatCount(Animation.INFINITE);
        imageView.setAnimation(rotation);
        rotation.start();

    }

}