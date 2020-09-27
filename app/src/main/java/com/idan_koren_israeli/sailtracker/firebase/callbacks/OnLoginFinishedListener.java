package com.idan_koren_israeli.sailtracker.firebase.callbacks;

import com.idan_koren_israeli.sailtracker.common.ClubMember;


public interface OnLoginFinishedListener {
    // Returns the member object associated with the authenticated user
    void onLoginFinished(ClubMember authenticatedMember);
}

