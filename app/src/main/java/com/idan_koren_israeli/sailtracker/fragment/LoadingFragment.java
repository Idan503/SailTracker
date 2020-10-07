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
import android.widget.TextView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.custom_views.SquareImageView;

import java.util.Objects;


public class LoadingFragment extends Fragment {

    private static final int DEFAULT_MESSAGE_ID = R.string.loading_msg_default;
    private static final int ANIMATION_DURATION = 3000;

    private View parent;

    private RotateAnimation rotateAnimation;
    private SquareImageView imageView;
    private TextView textView;
    private String message;
    private boolean showMessage;
    private boolean startHidden;


    public LoadingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onInflate(@NonNull Context context,@NonNull AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        TypedArray type = context.obtainStyledAttributes(attrs,R.styleable.LoadingFragment);


        //Retrieve attributes from xml
        showMessage = type.getBoolean(R.styleable.LoadingFragment_show_message,true);
        message = type.getString(R.styleable.LoadingFragment_message);
        startHidden = type.getBoolean(R.styleable.LoadingFragment_starts_hidden, true);


        if(message == null || message.equals(""))
            message = Objects.requireNonNull(getActivity()).getResources().getString(DEFAULT_MESSAGE_ID);

        Log.i("pttt", "Loading message " + message);

        type.recycle();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rotateAnimation = new RotateAnimation(0,360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(ANIMATION_DURATION);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parent = inflater.inflate(R.layout.fragment_loading, container, false);
        findViews(parent);
        imageView.setAnimation(rotateAnimation);

        applyAttributes();
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

        if(!showMessage)
            textView.setVisibility(View.GONE);
        else
            textView.setText(message);

    }

    public void show(){
        parent.setVisibility(View.VISIBLE);
        rotateAnimation.start();

    }

    public void hide(){
        parent.setVisibility(View.GONE);
        rotateAnimation.cancel();
    }

    public void setMessage(String message){
        textView.setText(message);
    }



}