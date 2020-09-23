package com.idan_koren_israeli.sailtracker.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.idan_koren_israeli.sailtracker.ClubMember;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class is the bridge between 2 (Firestore and FirebaseUser) out of 3 users-databases that are being used in this app.
 * User information which is profile picture and displayed name are stored on the authenticated user
 * while addition custom club properties like number of points and total sails are stored in Cloud Firestore
 */
public class FirestoreManager {
    Context context;
    FirebaseFirestore database;

    interface KEYS {
        String MEMBERS = "members";
    }

    @SuppressLint("StaticFieldLeak")
    private static FirestoreManager single_instance = null;
    // This WILL NOT cause a memory leak - *using application context only*

    private FirestoreManager(Context context) {
        database = FirebaseFirestore.getInstance();
        this.context = context;
    }

    public static FirestoreManager getInstance() {
        return single_instance;
    }

    public static FirestoreManager
    initHelper(Context context) {
        if (single_instance == null)
            single_instance = new FirestoreManager(context.getApplicationContext());
        return single_instance;
    }

    public ClubMember readMemberByUid(String uid){
        DocumentReference doc = database.collection(KEYS.MEMBERS).document(uid);
        final ClubMember[] result = {null};
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                result[0] = documentSnapshot.toObject(ClubMember.class);
            }
        });
        return result[0];
    }

    public void writeMember(ClubMember member){
        database.collection(KEYS.MEMBERS)
                .document(member.getUid())
                .set(member);
    }


    public boolean isMemberSaved(String uid){
        DocumentReference docRef = database.collection(KEYS.MEMBERS).document(uid);
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


}
