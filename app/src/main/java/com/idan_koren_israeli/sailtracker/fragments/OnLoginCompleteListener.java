package com.idan_koren_israeli.sailtracker.fragments;

import com.google.firebase.auth.FirebaseUser;
import com.idan_koren_israeli.sailtracker.ClubMember;

public interface OnLoginCompleteListener {
    // Returns the member object associated with the authenticated user
    void onLoginFinished(ClubMember authenticatedMember);
}
