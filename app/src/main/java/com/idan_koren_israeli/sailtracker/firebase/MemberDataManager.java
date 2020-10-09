package com.idan_koren_israeli.sailtracker.firebase;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnCheckFinishedListener;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnListLoadedListener;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnMemberLoadListener;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnGalleryPhotoLoadListener;
import com.idan_koren_israeli.sailtracker.club.GalleryPhoto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Using both Firestore and Storage Firabase components, to manage data of authenticated members.
 */
public class MemberDataManager {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore dbFirestore; // used for storing members information (as objects)
    private DatabaseReference dbRealtime;
    private FirebaseStorage dbStorage; // used for storing photos information (gallery and profile)
    private CommonUtils common;

    private ClubMember currentUser; // A lot of calls will use the current user, so it is stored as a property.
    private Boolean currentUserIsManager = null; // prevents multiple db calls for same question, null when unknown
    // This prevents redundant calls to the database (Over and over for the same current user's clubmember).

    private final static String TAG = "MemberDataManager";

    private static final int PHOTOS_QUALITY = 100; // Highest quality possible

    interface KEYS {
        String MEMBERS = "members";
        String PHONE_TO_MEMBERS = "phone_to_members";
        String LISTS = "lists";
        String MANAGERS = "managers";
        String ALL_MANAGERS = "all_managers";
        String ALL_MANAGERS_REGEX = ";";
        String GALLERY_PHOTOS = "gallery_photos";
        String PROFILE_PHOTOS = "profile_photos";
    }

    @SuppressLint("StaticFieldLeak")
    private static MemberDataManager single_instance = null;
    // This WILL NOT cause a memory leak - *using application context only*

    private MemberDataManager() {
        firebaseAuth = FirebaseAuth.getInstance();
        dbFirestore = FirebaseFirestore.getInstance();
        dbStorage = FirebaseStorage.getInstance();
        dbRealtime = FirebaseDatabase.getInstance().getReference();
        common = CommonUtils.getInstance();
        if (firebaseAuth.getCurrentUser() != null)
            loadMember(firebaseAuth.getCurrentUser().getUid(), null);
    }

    public static MemberDataManager getInstance() {
        return single_instance;
    }

    public static MemberDataManager
    initHelper() {
        if (single_instance == null) {
            single_instance = new MemberDataManager();
        }
        return single_instance;
    }

    //region Members Functions

    // Uid is the primary key of the firestore database
    public void loadMember(String uid, final OnMemberLoadListener onMemberLoaded) {
        if (currentUser!=null && uid.equals(currentUser.getUid())){
            onMemberLoaded.onMemberLoad(currentUser);
            return; // currentuser is already loaded
        }

        DocumentReference doc = dbFirestore.collection(KEYS.MEMBERS).document(uid);
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(onMemberLoaded!=null)
                    onMemberLoaded.onMemberLoad(documentSnapshot.toObject(ClubMember.class));
            }
        });
    }

    public void loadMembersList(List<String> uidsToLoad, final OnListLoadedListener<ClubMember> onLoaded){
        //Using query to firestore database to get all uids

        if(uidsToLoad.isEmpty()){
            onLoaded.onListLoaded(new ArrayList<ClubMember>());
            return;
        }

        OnCompleteListener<QuerySnapshot> listLoadComplete = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<ClubMember> result = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        result.add(document.toObject(ClubMember.class));
                    }
                    onLoaded.onListLoaded(result);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        };

        CollectionReference allMembers = dbFirestore.collection(KEYS.MEMBERS);
        allMembers.whereIn("uid", uidsToLoad).get().addOnCompleteListener(listLoadComplete);
    }

    public void loadCurrentMember(String uid, final OnMemberLoadListener onMemberLoaded){
        DocumentReference doc = dbFirestore.collection(KEYS.MEMBERS).document(uid);
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ClubMember loadedMember = documentSnapshot.toObject(ClubMember.class);
                if(loadedMember!=null)
                    setCurrentMember(loadedMember);
                if(onMemberLoaded!=null) {
                    onMemberLoaded.onMemberLoad(loadedMember);
                }
            }
        });
    }

    // Writing a member as an object into firestore
    public void storeMember(ClubMember member) {
        dbFirestore.collection(KEYS.MEMBERS)
                .document(member.getUid())
                .set(member);

        dbRealtime.child(KEYS.PHONE_TO_MEMBERS)
                .child(member.getPhoneNumber())
                .setValue(member.getUid());
    }



    // checking if a member is already exists in the database
    public void isMemberStored(String uid, final OnCheckFinishedListener onFinish) {
        DocumentReference docRef = dbFirestore.collection(KEYS.MEMBERS).document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (onFinish != null) {
                        if (document != null && document.exists())
                            onFinish.onCheckFinished(true);
                        else
                            onFinish.onCheckFinished(false);
                    }
                }
            }
        });
    }

    public void loadMemberByPhone(String phoneNumber, final OnMemberLoadListener onMemberLoaded){
        if (currentUser!=null && phoneNumber.equals(currentUser.getPhoneNumber())){
            onMemberLoaded.onMemberLoad(currentUser);
            return; // currentuser is already loaded
        }

        dbRealtime.child(KEYS.PHONE_TO_MEMBERS).child(phoneNumber).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@Nullable DataSnapshot snapshot) {
                        if(snapshot==null || snapshot.getValue()==null)
                            onMemberLoaded.onMemberLoad(null); // Member could not be found
                        else {
                            String memberUid = Objects.requireNonNull(snapshot.getValue()).toString();
                            loadMember(memberUid, onMemberLoaded); // Load member by the uid we got from rtdb
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );

    }


    public ClubMember getCurrentUser() {
        return currentUser;
    }

    public void setCurrentMember(ClubMember currentUserMember) {
        currentUser = currentUserMember;
        currentUserIsManager = null;
        isMemberStored(currentUser.getUid(), onCurrentUserSearched);
    }


    private OnCheckFinishedListener onCurrentUserSearched = new OnCheckFinishedListener() {
        @Override
        public void onCheckFinished(boolean found) {
            if(!found) {
                Log.i("pttt", currentUser.toString());
                storeMember(currentUser);

            }
        }
    };


    // Checks if a certain member is saved as a club manager in the db
    private void isManagerMember(final ClubMember member, final OnCheckFinishedListener onFinish) {
        DocumentReference docRef = dbFirestore.collection(KEYS.LISTS).document(KEYS.MANAGERS);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (onFinish != null) {
                        if (document != null && document.exists()) {
                            // All managers are stored by uid in a single string called "all_managers"
                            String allManagersUidString = (String) document.get(KEYS.ALL_MANAGERS);
                            if(allManagersUidString!=null){
                                String[] allManagersUid = allManagersUidString.split(KEYS.ALL_MANAGERS_REGEX);
                                for(String uid : allManagersUid){
                                    if(member.getUid().equals(uid)){
                                        if(member==currentUser)
                                            currentUserIsManager = true;
                                        onFinish.onCheckFinished(true); // member uid found in list of managers
                                        return;
                                    }
                                }
                                if(member==currentUser)
                                    currentUserIsManager = false;
                                onFinish.onCheckFinished(false); // not found in the list
                            }
                        }
                        else
                            onFinish.onCheckFinished(false);
                    }
                }

            }
        });
    }



    //endregion

    //region Profile Photo Functions

    // Getting current user member's profile photo link from storage, will be inserted into ui by Glide
    public void loadProfilePhoto(String uid, OnSuccessListener<Uri> onSuccess,
                                 OnFailureListener onFailure) {

        StorageReference profilePhotosHub = dbStorage.getReference().child(KEYS.PROFILE_PHOTOS);
        StorageReference filePath = profilePhotosHub.child(uid);

        filePath.getDownloadUrl().addOnSuccessListener(onSuccess).addOnFailureListener(onFailure);
    }

    // Each user can upload a profile image to his own unique folder
    public void storeProfilePhoto(String uid, Bitmap photo,
                                  OnSuccessListener<UploadTask.TaskSnapshot> onSuccess,
                                  OnFailureListener onFailure) {

        byte[] bytes = common.convertBitmapToBytes(photo, PHOTOS_QUALITY);
        StorageReference allProfilePhotos = dbStorage.getReference().child(KEYS.PROFILE_PHOTOS);
        StorageReference filePath = allProfilePhotos.child(uid);
        // Each member has a unique photo named on its uid
        filePath.putBytes(bytes).addOnSuccessListener(onSuccess).addOnFailureListener(onFailure);
    }

    //endregion

    //region Gallery Functions

    // Getting current user member's profile photo link from storage, will be inserted into ui by Glide
    public void loadGallery(String uid, final OnGalleryPhotoLoadListener onSinglePhotoLoaded) {

        // Folder success
        OnSuccessListener<ListResult> galleryFolderSuccess = new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult gallery) {
                for (final StorageReference photo : gallery.getItems()) {
                    String photoTimeStamp = CommonUtils.getInstance().toNumericString(photo.getName());
                    final long time = Long.parseLong(photoTimeStamp);
                    // All photos names are the times that they where taken in
                    // This prevents double-listeners concurrently, sync for uri AND metadata of each file
                    // This might be changed later.

                    photo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if(onSinglePhotoLoaded!=null)
                                onSinglePhotoLoaded.onPhotoLoaded(new GalleryPhoto(uri.toString(), time));
                        }
                    });
                }
            }
        };

        StorageReference galleryPhotosHub = dbStorage.getReference().child(MemberDataManager.KEYS.GALLERY_PHOTOS);
        StorageReference memberGalleryPath = galleryPhotosHub.child(uid);

        // Getting the parent folder of current user gallery
        memberGalleryPath.listAll().addOnSuccessListener(galleryFolderSuccess);
    }



    // Uploads an image into storage database, as a part of current user gallery
    // Therefore, each authenticated user can only upload gallery to his own unique folder
    public void storeGalleryPhoto(String memberUid,
                                  Bitmap photo,
                                  OnSuccessListener<UploadTask.TaskSnapshot> onSuccess,
                                  OnFailureListener onFailure,
                                  OnProgressListener<UploadTask.TaskSnapshot> onProgress) {

        byte[] bytes = common.convertBitmapToBytes(photo, PHOTOS_QUALITY);
        String fileName = Long.toString(Timestamp.now().getSeconds());
        // Name of picture is its time, user will not take 2 pictures in the same second
        // This will be helpful to sort pictures by time without the need of 2 concurrent callbacks (uri & time - metadata)
        // This might be changed later on.
        StorageReference allGalleryPhotos = dbStorage.getReference().child(KEYS.GALLERY_PHOTOS);
        StorageReference filePath = allGalleryPhotos.child(memberUid).child(fileName);
        // Each member has a unique sub-folder of photos
        filePath.putBytes(bytes).
                addOnSuccessListener(onSuccess).addOnFailureListener(onFailure).addOnProgressListener(onProgress);
    }


    public void deleteGalleryPhoto(String memberUid, GalleryPhoto photo,
                                   OnSuccessListener<? super Void> onSuccess,
                                   OnFailureListener onFailure){
        String fileName = Long.toString(photo.getTimeCreated());
        StorageReference allGalleryPhotos = dbStorage.getReference().child(KEYS.GALLERY_PHOTOS);
        StorageReference filePath = allGalleryPhotos.child(memberUid).child(fileName);
        filePath.delete().addOnSuccessListener(onSuccess).addOnFailureListener(onFailure);

    }

    //endregion



    //region Method Overloading: no uid parameter -> apply to currentUser
    public void loadProfilePhoto(OnSuccessListener<Uri> onSuccess,
                                 OnFailureListener onFailure) {
        loadProfilePhoto(currentUser.getUid(), onSuccess, onFailure);
    }

    public void storeProfilePhoto(Bitmap photo,
                                  OnSuccessListener<UploadTask.TaskSnapshot> onSuccess,
                                  OnFailureListener onFailure) {
        storeProfilePhoto(currentUser.getUid(), photo, onSuccess, onFailure);
    }

    public void loadGallery(final OnGalleryPhotoLoadListener onLoaded) {
        loadGallery(currentUser.getUid(),onLoaded);
    }

    public void storeGalleryPhoto(Bitmap photo,
                                  OnSuccessListener<UploadTask.TaskSnapshot> onSuccess,
                                  OnFailureListener onFailure,
                                  OnProgressListener<UploadTask.TaskSnapshot> onProgress) {
        storeGalleryPhoto(currentUser.getUid(), photo, onSuccess, onFailure, onProgress);
    }

    public void isManagerMember(final OnCheckFinishedListener onFinish){
        if(currentUserIsManager != null) // answer is already known
            onFinish.onCheckFinished(currentUserIsManager); // prevent multiple db calls
        isManagerMember(currentUser,onFinish);
    }


    //endregion


}
