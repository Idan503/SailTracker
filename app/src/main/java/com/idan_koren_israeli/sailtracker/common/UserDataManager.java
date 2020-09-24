package com.idan_koren_israeli.sailtracker.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.idan_koren_israeli.sailtracker.ClubMember;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * Using both Firestore and Storage Firabase components, to manage data of authenticated members.
 *
 */
public class UserDataManager {
    private Context context;
    private FirebaseFirestore dbFirestore; // used for storing members information (as objects)
    private FirebaseStorage dbStorage; // used for storing photos information
    private CommonUtils common;

    private static final int PHOTOS_QUALITY = 100;

    interface KEYS {
        String MEMBERS = "members";
        String GALLERY_PHOTOS = "gallery_photos";
        String PROFILE_PHOTOS = "profile_photos";
    }

    @SuppressLint("StaticFieldLeak")
    private static UserDataManager single_instance = null;
    // This WILL NOT cause a memory leak - *using application context only*

    private UserDataManager(Context context) {
        dbFirestore = FirebaseFirestore.getInstance();
        dbStorage = FirebaseStorage.getInstance();
        common = CommonUtils.getInstance();
        this.context = context;
    }

    public static UserDataManager getInstance() {
        return single_instance;
    }

    public static UserDataManager
    initHelper(Context context) {
        if (single_instance == null)
            single_instance = new UserDataManager(context.getApplicationContext());
        return single_instance;
    }

    // Uid is the primary key of the firestore database
    public ClubMember readMemberByUid(String uid){
        DocumentReference doc = dbFirestore.collection(KEYS.MEMBERS).document(uid);
        final ClubMember[] result = {null};
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                result[0] = documentSnapshot.toObject(ClubMember.class);
            }
        });
        return result[0];
    }

    // Writing a member as an object into firestore
    public void writeMember(ClubMember member){
        dbFirestore.collection(KEYS.MEMBERS)
                .document(member.getUid())
                .set(member);
    }

    // checking if a club member is already exists in the database
    public boolean isMemberSaved(String uid){
        DocumentReference docRef = dbFirestore.collection(KEYS.MEMBERS).document(uid);
        final boolean[] isExists = {false};
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document!=null && document.exists())
                        isExists[0] = true;
                }
            }
        });
        return isExists[0];
    }

    // Converting the object that is the output of the log-in system to the object that is saved on the database
    private ClubMember convertUserToClubMember(FirebaseUser user){
        ClubMember member;
        // Saving the user in the database if its a new one, otherwise, returns existing object data.
        if(!isMemberSaved(user.getUid())) {
            member = new ClubMember(user);
            writeMember(member);
        }
        else{
            member = readMemberByUid(user.getUid());
        }
        return member;
    }

    // Returns the user's own member object
    public ClubMember getCurrentMember(){
        FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
        if(authUser!=null)
            return convertUserToClubMember(authUser);
        return null;
    }


    // Getting current user member's profile photo link from storage, will be inserted into ui by Glide
    public void getProfilePhoto(OnSuccessListener<Uri> onSuccess,
                                  OnFailureListener onFailure){
        ClubMember member = getCurrentMember();
        if(member==null)
            return;

        StorageReference allProfilePhotos = dbStorage.getReference().child(KEYS.PROFILE_PHOTOS);
        StorageReference filePath = allProfilePhotos.child(getCurrentMember().getUid());

        filePath.getDownloadUrl().addOnSuccessListener(onSuccess).addOnFailureListener(onFailure);

    }


    // Uploads an image into storage database, as a part of current user gallery
    // Therefore, each authenticated user can only upload gallery to his own unique folder
    public void uploadGalleryPhoto(Bitmap photo,
                                   OnSuccessListener<UploadTask.TaskSnapshot> onSuccess,
                                   OnFailureListener onFailure) {
        ClubMember member = getCurrentMember();
        if(member==null)
            return;

        byte[] bytes = convertBitmapToBytes(photo);
        String fileName = UUID.randomUUID().toString(); // Randomized unique ID
        StorageReference allGalleryPhotos = dbStorage.getReference().child(KEYS.GALLERY_PHOTOS);
        StorageReference filePath = allGalleryPhotos.child(getCurrentMember().getUid()).child(fileName);
        // Each member has a unique sub-folder of photos
        filePath.putBytes(bytes).addOnSuccessListener(onSuccess).addOnFailureListener(onFailure);
    }

    // Each user can upload a profile image to his own unique folder
    public void uploadProfileImage(Bitmap photo,
                                   OnSuccessListener<UploadTask.TaskSnapshot> onSuccess,
                                   OnFailureListener onFailure) {
        ClubMember member = getCurrentMember();
        if(member==null)
            return;

        Log.i("pttt", "test4");
        byte[] bytes = convertBitmapToBytes(photo);
        StorageReference allProfilePhotos = dbStorage.getReference().child(KEYS.PROFILE_PHOTOS);
        StorageReference filePath = allProfilePhotos.child(getCurrentMember().getUid());
        // Each member has a unique photo named on its uid
        filePath.putBytes(bytes).addOnSuccessListener(onSuccess).addOnFailureListener(onFailure);
    }

    private byte[] convertBitmapToBytes(Bitmap photo){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, PHOTOS_QUALITY, stream);

        return stream.toByteArray();
    }





}
