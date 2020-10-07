package com.idan_koren_israeli.sailtracker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.firebase.LoginManager;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnMemberLoadListener;

public class SplashActivity extends AppCompatActivity {

    private ImageView appIcon;
    private static final int DURATION = 1250; //in ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Loading already authenticated user (if exists) into local data while splashscreen is showing
        FirebaseUser authenticatedUser = FirebaseAuth.getInstance().getCurrentUser();
        if(authenticatedUser!=null){
            MemberDataManager.getInstance().loadCurrentMember(authenticatedUser.getUid(),null);
        }


        findViews();
        applySource();
        applyAnimation();


    }

    private OnMemberLoadListener onCurrentMemberLoaded = new OnMemberLoadListener() {
        @Override
        public void onMemberLoad(ClubMember memberLoaded) {
            startApp();
        }
    };

    private void findViews(){
        appIcon = findViewById(R.id.splash_IMG_icon);

    }

    private void applySource(){
        CommonUtils.getInstance().setImageResource(appIcon, R.drawable.ic_launcher_foreground);
    }

    private void applyAnimation(){
        appIcon.setScaleX(0.6f);
        appIcon.setScaleY(0.6f);
        appIcon.setAlpha(0.0f);
        appIcon.animate()
                .alpha(1.0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(DURATION)
                .setInterpolator(new LinearInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        appIcon.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        startApp();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) { }

                    @Override
                    public void onAnimationRepeat(Animator animator) { }
                });
    }
    
    private void startApp(){
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}