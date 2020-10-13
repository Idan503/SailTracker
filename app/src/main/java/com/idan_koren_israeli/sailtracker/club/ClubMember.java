package com.idan_koren_israeli.sailtracker.club;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.idan_koren_israeli.sailtracker.club.exception.NotEnoughPointsException;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * This class provides a member which is a single end-user of the app.
 * Picture, uid and name and phone number are the same as the authenticated user.
 *
 * uid is the primary key that shared between ClubMember and Firebase authentication
 *
 */
public class ClubMember implements Serializable {
    private String uid;
    private String name;
    private String phoneNumber;
    private int pointsCount;
    private int eventCount;
    private ArrayList<GalleryPhoto> galleryPhotos;

    public ClubMember(){

    }

    public ClubMember(String uid, String pictureURI, String name, String phoneNumber, int pointsCount, int eventCount) {
        this.uid = uid;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.pointsCount = pointsCount;
        this.eventCount = eventCount;
        this.galleryPhotos = new ArrayList<>();
    }


    // Generating a new club member based on its authenticated object only
    public ClubMember(String uid, String displayName, String phoneNumber){
        this.name = displayName;
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.pointsCount = 0;
        this.eventCount = 0;
        this.galleryPhotos = new ArrayList<>();
    }



    //region Getters & Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public void setPointsCount(int pointsCount) {
        this.pointsCount = pointsCount;
    }

    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }

    // Gallery photos are saved via Storage component and not Firestore
    public ArrayList<GalleryPhoto> getGalleryPhotos(){
        if(galleryPhotos==null)
            initGalleryPhotos();
        return this.galleryPhotos;
    }

    //endregion

    public void addGalleryPhoto(GalleryPhoto photo){
        if(galleryPhotos==null)
            initGalleryPhotos();
        if(!galleryPhotos.contains(photo))
            galleryPhotos.add(photo);
    }

    public void removeGalleryPhoto(GalleryPhoto photo){
        galleryPhotos.remove(photo);
    }

    public void deductPoints(int count) throws NotEnoughPointsException {
        if(pointsCount - count < 0)
            throw new NotEnoughPointsException("Member " + name + " has less than " + count + " points");
        this.pointsCount-=count;
    }

    public void addOneEvent(){
        this.eventCount++;
    }

    public void deductOneEvent(){
        this.eventCount--;
    }

    public void addPoints(int count){
        this.pointsCount+=count;
    }


    private void initGalleryPhotos(){
        this.galleryPhotos = new ArrayList<>();
    }

    @NonNull
    @Override      
    public String toString(){
        return "ID: " + uid
                + "\nName: "+ name
                + "\nPhone: " +phoneNumber
                + "\nNumber of Sails: " + eventCount
                + "\nNumber of Points: " + pointsCount;
    }

}
