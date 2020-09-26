package com.idan_koren_israeli.sailtracker.firebase.callbacks;

import com.idan_koren_israeli.sailtracker.common.ClubMember;

/**
 * Those callbacks are created in order to control firebase tasks
 */

// After a
public interface OnFileSearchFinishListener {
    void onFileSearchFinish(boolean found);
}
