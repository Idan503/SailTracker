package com.idan_koren_israeli.sailtracker.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.fragments.LoginFragment;

public class HomeActivity extends BaseActivity {

    FirebaseUser user;

    LinearLayout loginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViews();
        user = FirebaseAuth.getInstance().getCurrentUser();


        if(isUserLoggedIn()){
            Log.i("pttt","Logged in user: " + user.getDisplayName() + " | Phone: " + user.getPhoneNumber());
            removeLoginFragment(); // No need for login
        }
        else{
            // start log in fragment
            loadLoginFragment();

        }
    }

    private void findViews(){
        loginLayout = findViewById(R.id.home_LAY_login);
    }

    private void loadLoginFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.home_FRAG_login_placeholder, new LoginFragment());
        ft.commit();
    }

    private void removeLoginFragment(){
        loginLayout.setVisibility(View.GONE);
    }

    private boolean isUserLoggedIn(){
        return user!=null;
    }
}