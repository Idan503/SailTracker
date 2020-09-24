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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.idan_koren_israeli.sailtracker.ClubMember;
import com.idan_koren_israeli.sailtracker.gallery.GalleryPhoto;

import java.io.ByteArrayOutputStream;

/**
 * Using both Firestore and Storage Firabase components, to manage data of authenticated members.
 *
 */
public class DatabaseManager {
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
    private static DatabaseManager single_instance = null;
    // This WILL NOT cause a memory leak - *using application context only*

    private DatabaseManager(Context context) {
        dbFirestore = FirebaseFirestore.getInstance();
        dbStorage = FirebaseStorage.getInstance();
        common = CommonUtils.getInstance();
        this.context = context;
    }

    public static DatabaseManager getInstance() {
        return single_instance;
    }

    public static DatabaseManager
    initHelper(Context context) {
        if (single_instance == null)
            single_instance = new DatabaseManager(context.getApplicationContext());
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
    public void readProfilePhoto(OnSuccessListener<Uri> onSuccess,
                                 OnFailureListener onFailure){
        ClubMember member = getCurrentMember();
        if(member==null)
            return;

        StorageReference profilePhotosHub = dbStorage.getReference().child(KEYS.PROFILE_PHOTOS);
        StorageReference filePath = profilePhotosHub.child(getCurrentMember().getUid());

        filePath.getDownloadUrl().addOnSuccessListener(onSuccess).addOnFailureListener(onFailure);

    }

    // Getting current user member's profile photo link from storage, will be inserted into ui by Glide
    public void readMemberGallery(){
        ClubMember member = DatabaseManager.getInstance().getCurrentMember();
        if(member==null)
            return;


        StorageReference galleryPhotosHub = dbStorage.getReference().child(DatabaseManager.KEYS.GALLERY_PHOTOS);
        StorageReference memberGalleryPath = galleryPhotosHub.child(getCurrentMember().getUid());

        // Getting the parent folder of current user gallery
        memberGalleryPath.listAll().addOnSuccessListener(galleryFolderSuccess);
    }


    // Calls when we have managed to get into the folder contains photos of user gallery
    private OnSuccessListener<ListResult> galleryFolderSuccess = new OnSuccessListener<ListResult>() {
        @Override
        public void onSuccess(ListResult gallery) {
            for(final StorageReference photo : gallery.getItems()) {
                final long time = Long.parseLong(photo.getName());
                // All photos names are the times that they where taken in
                // This prevents double-listeners concurrently, sync for uri AND metadata of each file
                // This might be changed later.
                photo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        GalleryPhoto photo = new GalleryPhoto(uri, time);
                        getCurrentMember().addGalleryPhoto(photo);
                    }
                });
            }
        }
    };







    // Uploads an image into storage database, as a part of current user gallery
    // Therefore, each authenticated user can only upload gallery to his own unique folder
    public void uploadGalleryPhoto(Bitmap photo,
                                   OnSuccessListener<UploadTask.TaskSnapshot> onSuccess,
                                   OnFailureListener onFailure) {
        ClubMember member = getCurrentMember();
        if(member==null)
            return;

        byte[] bytes = convertBitmapToBytes(photo);
        String fileName = Long.toString(Timestamp.now().getSeconds());
        // Name of picture is its time, user will not take 2 pictures in the same second
        // This will be helpful to sort pictures by time without the need of 2 concurrent callbacks (uri & time - metadata)
        // This might be changed later on.
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
