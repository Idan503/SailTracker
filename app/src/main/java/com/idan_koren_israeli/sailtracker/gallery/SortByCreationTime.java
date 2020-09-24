package com.idan_koren_israeli.sailtracker.gallery;

import java.util.Comparator;

/**
 * When we get the photos from firebase storage, we would like to get them sorted by creation time
 * This comparator will use the matadata of firebase saved files and compare by creation time
 *
 * */
public class SortByCreationTime implements Comparator<GalleryPhoto> {
    @Override
    public int compare( GalleryPhoto sr1, GalleryPhoto sr2) {
        return (int) (sr2.getTimeCreated() - sr1.getTimeCreated());
    }
}
