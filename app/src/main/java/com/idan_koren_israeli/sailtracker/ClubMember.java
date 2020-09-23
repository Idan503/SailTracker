package com.idan_koren_israeli.sailtracker;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


/**
 * This class provides a member which is a single end-user of the app.
 * Picture, uid and name and phone number are the same as the authenticated user.
 *
 * Unfortunately, there is no option to save firebase's authenticated user object on the database,
 * so relevant properties of firebase's user and club member are merged into this class.
 *
 * uid is the key that is shared between ClubMember and Firebase authentication
 *
 */
public class ClubMember {


    private String uid;
    private String profilePictureUrl;
    private String name;
    private String phoneNumber;
    private int pointsCount;
    private int sailsCount;
    // ArrayList<Pictures>

    public ClubMember(){

    }

    public ClubMember(String uid, String pictureURI, String name, String phoneNumber, int pointsCount, int sailsCount) {
        this.uid = uid;
        this.profilePictureUrl = pictureURI;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.pointsCount = pointsCount;
        this.sailsCount = sailsCount;
    }

    // Generating a new club member based on its authenticated object only
    public ClubMember(FirebaseUser firebaseUser){
        this.name = firebaseUser.getDisplayName();
        this.uid = firebaseUser.getUid();
        this.phoneNumber = firebaseUser.getPhoneNumber();
        this.profilePictureUrl = null;
        this.pointsCount = 0;
        this.sailsCount = 0;
    }

    // Generating a new club member based on its authenticated object only
    public ClubMember(String uid, String displayName, String phoneNumber){
        this.name = displayName;
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = null;
        this.pointsCount = 0;
        this.sailsCount = 0;
    }



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String url) {
        this.profilePictureUrl = url;
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

    public int getSailsCount() {
        return sailsCount;
    }

    public void setSailsCount(int sailsCount) {
        this.sailsCount = sailsCount;
    }


    @NonNull
    @Override      
    public String toString(){
        return "ID: " + uid
                + "\nName: "+ name
                + "\nPhone: " +phoneNumber
                + "\nNumber of Sails: " + sailsCount
                + "\nNumber of Points: " + pointsCount
                + "\nHave a profile picture: " + (profilePictureUrl!=null);
    }

}