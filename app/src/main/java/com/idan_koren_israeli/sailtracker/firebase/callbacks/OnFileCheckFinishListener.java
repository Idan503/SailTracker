package com.idan_koren_israeli.sailtracker.firebase.callbacks;

/**
 * Those callbacks are created in order to control firebase tasks
 */

// Checking a certain file for a boolean answer
public interface OnFileCheckFinishListener {
    void onCheckFinished(boolean result);
}
