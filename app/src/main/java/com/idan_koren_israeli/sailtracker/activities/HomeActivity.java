package com.idan_koren_israeli.sailtracker.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.idan_koren_israeli.sailtracker.OnLoginCompleteListener;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.fragments.LoginFragment;
import com.idan_koren_israeli.sailtracker.fragments.ProfileFragment;

public class HomeActivity extends BaseActivity {

    FirebaseUser user;


    LinearLayout loginLayout;
    ProfileFragment profileFragment;
    LoginFragment loginFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViews();
        loginFragment.setOnCompleteListener(loginCompleteListener);

        user = FirebaseAuth.getInstance().getCurrentUser();



        if(isUserLoggedIn()){
            Log.i("pttt","Logged in user: " + user.getDisplayName() + " | Phone: " + user.getPhoneNumber());
            hideLoginFragment(); // No need for login
            updateInterfaceToUser(user);
        }
//        else{
            // log in fragment is starting from xml


    }

    private void findViews(){
        loginLayout = findViewById(R.id.home_LAY_login);
        profileFragment =(ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.home_FRAG_profile);
        loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.home_FRAG_login);

    }

    private void hideLoginFragment(){
        loginLayout.setVisibility(View.GONE);
    }

    private boolean isUserLoggedIn(){
        return user!=null;
    }

    private OnLoginCompleteListener loginCompleteListener = new OnLoginCompleteListener() {
        @Override
        public void onLoginFinished() {
            hideLoginFragment();
            profileFragment.updateDisplayData(user);
        }
    };

    private void updateInterfaceToUser(FirebaseUser user){
        profileFragment.updateDisplayData(user);
    }

}