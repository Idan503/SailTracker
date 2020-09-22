package com.idan_koren_israeli.sailtracker.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

/**
 * This class is the bridge between 2 out of 3 users-databases that are being used in this app
 * user information which is profile picture and displayed name are stored on the authenticated user
 * while addition custom club properties like number of points and total sails are stored in Cloud Firestore
 */
public class FirestoreManager {
    Context context;
    FirebaseFirestore database;

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


    // Searching for a specific authorized user additional properties in the database
    public int getPointsByUserID(FirebaseUser user) {
        database.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d("pttt", Boolean.toString(document.getData().containsKey("SailsCount")));
                                Log.d("pttt", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("pttt", "Error getting documents.", task.getException());
                        }
                    }
                });
        return 0;
    }

    public void writeNewUser(FirebaseUser user)
    {


    }


}
