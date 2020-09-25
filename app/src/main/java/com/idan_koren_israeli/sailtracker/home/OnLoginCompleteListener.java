package com.idan_koren_israeli.sailtracker.home;

import com.idan_koren_israeli.sailtracker.common.ClubMember;

public interface OnLoginCompleteListener {
    // Returns the member object associated with the authenticated user
    void onLoginFinished(ClubMember authenticatedMember);
}
