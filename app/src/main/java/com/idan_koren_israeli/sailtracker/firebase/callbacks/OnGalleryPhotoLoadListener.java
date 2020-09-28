package com.idan_koren_israeli.sailtracker.firebase.callbacks;

import com.idan_koren_israeli.sailtracker.gallery.GalleryPhoto;

/**
 * Those callbacks are created in order to control firebase tasks
 */

public interface OnGalleryPhotoLoadListener {
    void onPhotoLoaded(GalleryPhoto photo);
}
