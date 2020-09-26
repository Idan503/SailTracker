package com.idan_koren_israeli.sailtracker.firebase.callbacks;

import com.idan_koren_israeli.sailtracker.common.ClubMember;

/**
 * Those callbacks are created in order to control firebase tasks
 */

// Is a specific file found or not in the database
public interface OnMemberLoadListener {
    void onMemberLoad(ClubMember memberLoaded);
}
