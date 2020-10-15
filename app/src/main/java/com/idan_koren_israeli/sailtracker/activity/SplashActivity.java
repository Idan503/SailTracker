package com.idan_koren_israeli.sailtracker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.common.SharedPrefsManager;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnCheckFinishedListener;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnMemberLoadListener;

public class SplashActivity extends AppCompatActivity {

    private LinearLayout parentLayout;
    private ImageView iconImage;
    private TextView labelText;
    private static final int ANIMATION_DURATION = 1500; //in ms
    private static final int CHANGE_TEXT_DELAY = 500; //in ms

    private boolean startAppOnAnimationEnd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        SharedPrefsManager sp = SharedPrefsManager.getInstance();

        // Loading already authenticated user (if exists) into local data while splashscreen is showing
        String loggedPhone = sp.getString(SharedPrefsManager.KEYS.CURRENT_USER_PHONE,null);
        if(loggedPhone !=null){
            MemberDataManager.getInstance().isMemberStoredByPhone(loggedPhone, onCurrentMemberFound);
            MemberDataManager.getInstance().loadMemberByPhone(loggedPhone,onCurrentMemberLoaded);
        }
        else{
            startAppOnAnimationEnd = true;
            // Nothing to check w/ db, app can start
        }




        findViews();
        applySource();
        applyAnimation();


    }

    private OnMemberLoadListener onCurrentMemberLoaded = new OnMemberLoadListener() {
        @Override
        public void onMemberLoad(ClubMember memberLoaded) {
            if(memberLoaded!=null) {
                MemberDataManager.getInstance().setCurrentMember(memberLoaded);
            }
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
        parentLayout = findViewById(R.id.splash_LAY_parent);
        iconImage = findViewById(R.id.splash_IMG_icon);
        labelText = findViewById(R.id.splash_LBL_text);

    }

    private void applySource(){
        CommonUtils.getInstance().setImageResource(iconImage, R.drawable.ic_launcher_foreground);
    }

    private void applyAnimation(){
        parentLayout.setScaleX(0.55f);
        parentLayout.setScaleY(0.55f);
        parentLayout.setAlpha(0.0f);
        parentLayout.animate()
                .alpha(1.0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(ANIMATION_DURATION)
                .setInterpolator(new LinearInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if(startAppOnAnimationEnd)
                            startApp();
                        else
                        {
                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    labelText.setText(getResources().getString(R.string.label_loading_splash_screen));
                                }
                            }, CHANGE_TEXT_DELAY);

                        }


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