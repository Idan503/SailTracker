package com.idan_koren_israeli.sailtracker.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.idan_koren_israeli.sailtracker.ClubMember;
import com.idan_koren_israeli.sailtracker.fragments.OnLoginCompleteListener;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.common.MemberManager;
import com.idan_koren_israeli.sailtracker.fragments.LoginFragment;
import com.idan_koren_israeli.sailtracker.fragments.ProfileFragment;

public class HomeActivity extends BaseActivity {

    private ClubMember member; // the current specific user's member object
    private LinearLayout loginLayout;
    private ProfileFragment profileFragment;
    private LoginFragment loginFragment;

    private MemberManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbManager = MemberManager.getInstance();

        findViews();
        loginFragment.setOnCompleteListener(loginCompleteListener);


        if(loginFragment.isLoggedIn()){
            hideLoginFragment(); // User is already logged-in
            member = dbManager.getCurrentMember();
            if(member!=null)
                updateInterface();
            else{
                // problem detected - user is logged in but could got get object
                Log.e("pttt", "Could not get logged in user data");
            }
        }

    }

    private void findViews(){
        loginLayout = findViewById(R.id.home_LAY_login);
        profileFragment =(ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.home_FRAG_profile);
        loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.home_FRAG_login);

    }

    private void hideLoginFragment(){
        loginLayout.setVisibility(View.GONE);
    }



    private OnLoginCompleteListener loginCompleteListener = new OnLoginCompleteListener() {
        @Override
        public void onLoginFinished(ClubMember authenticatedMember) {
            member = authenticatedMember;
            hideLoginFragment();
            updateInterface();
            Log.i("pttt", "Finished Log-in B");
        }
    };

    // Making the data appears on screen to the current user's data
    private void updateInterface(){
        profileFragment.updateDisplayData(member);
    }



}