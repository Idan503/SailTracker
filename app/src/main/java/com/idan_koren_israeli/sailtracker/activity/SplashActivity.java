package com.idan_koren_israeli.sailtracker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.common.SharedPrefsManager;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnCheckFinishedListener;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnMemberLoadListener;

public class SplashActivity extends AppCompatActivity {

    private ImageView appIcon;
    private static final int DURATION = 1500; //in ms

    private SharedPrefsManager sp;
    private boolean startAppAsAnimationEnds = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sp = SharedPrefsManager.getInstance();

        // Loading already authenticated user (if exists) into local data while splashscreen is showing
        String loggedPhone = sp.getString(SharedPrefsManager.KEYS.CURRENT_USER_PHONE,null);
        if(loggedPhone !=null){
            MemberDataManager.getInstance().isMemberStoredByPhone(loggedPhone, onCurrentMemberFound);
            MemberDataManager.getInstance().loadMemberByPhone(loggedPhone,onCurrentMemberLoaded);
        }
        else{
            startAppAsAnimationEnds = true;
            // Nothing to check w/ db, app can start
        }


        findViews();
        applySource();
        applyAnimation();


    }

    private OnMemberLoadListener onCurrentMemberLoaded = new OnMemberLoadListener() {
        @Override
        public void onMemberLoad(ClubMember memberLoaded) {
            MemberDataManager.getInstance().setCurrentMember(memberLoaded);
            startApp();
            // User found and set, app can start
        }
    };

    private OnCheckFinishedListener onCurrentMemberFound = new OnCheckFinishedListener() {
        @Override
        public void onCheckFinished(boolean result) {
            if(!result){
                startApp();
                // User is not found in the database, start app to login
            }
        }
    };

    private void findViews(){
        appIcon = findViewById(R.id.splash_IMG_icon);

    }

    private void applySource(){
        CommonUtils.getInstance().setImageResource(appIcon, R.drawable.ic_launcher_foreground);
    }

    private void applyAnimation(){
        appIcon.setScaleX(0.55f);
        appIcon.setScaleY(0.55f);
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

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if(startAppAsAnimationEnds)
                            startApp();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
    }
    
    private void startApp(){
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}