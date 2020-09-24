package com.idan_koren_israeli.sailtracker.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;

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
 * Using both Firestore and Storage Firabase components, to manager data of authenticated members.
 *
 */
public class MemberManager {
    private Context context;
    private FirebaseFirestore dbFirestore; // used for storing members information (as objects)
    private FirebaseStorage dbStorage; // used for storing photos information
    private CommonUtils common;

    private static final int PHOTOS_QUALITY = 100;

    interface KEYS {
        String MEMBERS = "members";
        String GALLERY_IMAGES = "gallery_images";
        String PROFILE_IMAGES = "profile_images";
    }

    @SuppressLint("StaticFieldLeak")
    private static MemberManager single_instance = null;
    // This WILL NOT cause a memory leak - *using application context only*

    private MemberManager(Context context) {
        dbFirestore = FirebaseFirestore.getInstance();
        dbStorage = FirebaseStorage.getInstance();
        common = CommonUtils.getInstance();
        this.context = context;
    }

    public static MemberManager getInstance() {
        return single_instance;
    }

    public static MemberManager
    initHelper(Context context) {
        if (single_instance == null)
            single_instance = new MemberManager(context.getApplicationContext());
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
        StorageReference filePath = dbStorage.getReference().child(KEYS.GALLERY_IMAGES)
                .child(getCurrentMember().getUid()).child(fileName); // Each member has a unique sub-folder of images
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
        String fileName = UUID.randomUUID().toString(); // Randomized unique ID
        StorageReference filePath = dbStorage.getReference().child(KEYS.PROFILE_IMAGES)
                .child(getCurrentMember().getUid()).child(fileName); // Each member has a unique sub-folder of images
        filePath.putBytes(bytes).addOnSuccessListener(onSuccess).addOnFailureListener(onFailure);
    }

    private byte[] convertBitmapToBytes(Bitmap photo){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, PHOTOS_QUALITY, stream);

        return stream.toByteArray();
    }




}
