package com.idan_koren_israeli.sailtracker;

import com.google.firebase.auth.FirebaseUser;

public interface OnLoginCompleteListener {
    void onLoginFinished(FirebaseUser authenticatedUser);
}
