package com.idan_koren_israeli.sailtracker.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.idan_koren_israeli.sailtracker.ClubMember;
import com.idan_koren_israeli.sailtracker.OnLoginCompleteListener;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.common.FirestoreManager;
import com.idan_koren_israeli.sailtracker.fragments.LoginFragment;
import com.idan_koren_israeli.sailtracker.fragments.ProfileFragment;

public class HomeActivity extends BaseActivity {

    private ClubMember member; // the current specific user's member object
    private LinearLayout loginLayout;
    private ProfileFragment profileFragment;
    private LoginFragment loginFragment;

    private FirestoreManager dbManager;
    private FirebaseAuth authManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbManager = FirestoreManager.getInstance();
        authManager = FirebaseAuth.getInstance();

        findViews();
        loginFragment.setOnCompleteListener(loginCompleteListener);


        if(isUserLoggedIn()){
            hideLoginFragment(); // No need for login
            if(authManager.getCurrentUser()!=null) {
                member = dbManager.convertUserToClubMember(authManager.getCurrentUser());
                updateInterfaceToUser(member);
            }
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

    private boolean isUserLoggedIn(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return firebaseUser!=null;
    }

    private OnLoginCompleteListener loginCompleteListener = new OnLoginCompleteListener() {
        @Override
        public void onLoginFinished(FirebaseUser authenticatedUser) {
            member = FirestoreManager.getInstance().convertUserToClubMember(authenticatedUser);
            hideLoginFragment();
            updateInterfaceToUser(member);
        }
    };

    // Making the data appears on screen to the current user's data
    private void updateInterfaceToUser(ClubMember member){
        profileFragment.updateDisplayData(member);
    }



}