package com.idan_koren_israeli.sailtracker.common;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnFileSearchFinishListener;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnMemberLoadListener;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnGalleryPhotoLoadListener;
import com.idan_koren_israeli.sailtracker.gallery.GalleryPhoto;

/**
 * Using both Firestore and Storage Firabase components, to manage data of authenticated members.
 */
public class DatabaseManager {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore dbFirestore; // used for storing members information (as objects)
    private FirebaseStorage dbStorage; // used for storing photos information (gallery and profile)
    private CommonUtils common;

    private ClubMember currentUser; // A lot of calls will use the current user, so it is stored as a property.
    // This prevents redundant calls to the database (Over and over for the same current user's clubmember).

    private static final int PHOTOS_QUALITY = 100;

    interface KEYS {
        String MEMBERS = "members";
        String GALLERY_PHOTOS = "gallery_photos";
        String PROFILE_PHOTOS = "profile_photos";
    }

    @SuppressLint("StaticFieldLeak")
    private static DatabaseManager single_instance = null;
    // This WILL NOT cause a memory leak - *using application context only*

    private DatabaseManager() {
        firebaseAuth = FirebaseAuth.getInstance();
        dbFirestore = FirebaseFirestore.getInstance();
        dbStorage = FirebaseStorage.getInstance();
        common = CommonUtils.getInstance();
        if (firebaseAuth.getCurrentUser() != null)
            loadMember(firebaseAuth.getCurrentUser().getUid(), null);
    }

    public static DatabaseManager getInstance() {
        return single_instance;
    }

    public static DatabaseManager
    initHelper() {
        if (single_instance == null) {
            single_instance = new DatabaseManager();
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

    // Writing a member as an object into firestore
    public void storeMember(ClubMember member) {
        dbFirestore.collection(KEYS.MEMBERS)
                .document(member.getUid())
                .set(member);
    }

    // checking if a member is already exists in the database
    public void isMemberStored(String uid, final OnFileSearchFinishListener onFinish) {
        DocumentReference docRef = dbFirestore.collection(KEYS.MEMBERS).document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (onFinish != null) {
                        if (document != null && document.exists())
                            onFinish.onFileSearchFinish(true);
                        else
                            onFinish.onFileSearchFinish(false);
                    }
                }

            }
        });
    }


    public ClubMember getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(ClubMember currentUserMember) {
        currentUser = currentUserMember;
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
    public void loadGallery(String uid, final OnGalleryPhotoLoadListener onPhotoLoaded) {

        // Folder success
        OnSuccessListener<ListResult> galleryFolderSuccess = new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult gallery) {
                for (final StorageReference photo : gallery.getItems()) {
                    final long time = Long.parseLong(photo.getName());
                    // All photos names are the times that they where taken in
                    // This prevents double-listeners concurrently, sync for uri AND metadata of each file
                    // This might be changed later.

                    photo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if(onPhotoLoaded!=null)
                                onPhotoLoaded.onPhotoLoaded(new GalleryPhoto(uri, time));
                        }
                    });
                }
            }
        };

        StorageReference galleryPhotosHub = dbStorage.getReference().child(DatabaseManager.KEYS.GALLERY_PHOTOS);
        StorageReference memberGalleryPath = galleryPhotosHub.child(uid);

        // Getting the parent folder of current user gallery
        memberGalleryPath.listAll().addOnSuccessListener(galleryFolderSuccess);
    }



    // Uploads an image into storage database, as a part of current user gallery
    // Therefore, each authenticated user can only upload gallery to his own unique folder
    public void storeGalleryPhoto(String uid,
                                  Bitmap photo,
                                  OnSuccessListener<UploadTask.TaskSnapshot> onSuccess,
                                  OnFailureListener onFailure) {

        byte[] bytes = common.convertBitmapToBytes(photo, PHOTOS_QUALITY);
        String fileName = Long.toString(Timestamp.now().getSeconds());
        // Name of picture is its time, user will not take 2 pictures in the same second
        // This will be helpful to sort pictures by time without the need of 2 concurrent callbacks (uri & time - metadata)
        // This might be changed later on.
        StorageReference allGalleryPhotos = dbStorage.getReference().child(KEYS.GALLERY_PHOTOS);
        StorageReference filePath = allGalleryPhotos.child(uid).child(fileName);
        // Each member has a unique sub-folder of photos
        filePath.putBytes(bytes).addOnSuccessListener(onSuccess).addOnFailureListener(onFailure);
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
                                  OnFailureListener onFailure) {
        storeGalleryPhoto(currentUser.getUid(), photo, onSuccess, onFailure);
    }


    //endregion


}
