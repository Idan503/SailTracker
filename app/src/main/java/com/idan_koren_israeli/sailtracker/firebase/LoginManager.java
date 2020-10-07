package com.idan_koren_israeli.sailtracker.firebase;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;

import java.util.concurrent.TimeUnit;

public class LoginManager {
    private static LoginManager single_instance = null;
    public enum LoginState { NOT_STARTED,IN_PROGRESS, CODE_SENT, CODE_APPROVED, COMPLETED, FAILED}

    private FirebaseAuth firebaseAuth;
    private String mVerificationId;
    private LoginState currentState;
    private Activity callerActivity;
    private String authenticatedId; // id of user that is logged in successfully

    private LoginManager(){
        firebaseAuth = FirebaseAuth.getInstance();
        currentState = LoginState.NOT_STARTED;
        firebaseAuth.useAppLanguage();
        authenticatedId = null;
    }

    public void setCallerActivity(Activity callerActivity) {
        this.callerActivity = callerActivity;
    }

    public static LoginManager
    initHelper(){
        if(single_instance == null)
            single_instance = new LoginManager();
        return single_instance;
    }

    public static LoginManager getInstance(@NonNull Activity caller) {
        single_instance.setCallerActivity(caller);
        return single_instance;
    }

    public boolean isUserAuthenticated(){
        return firebaseAuth.getCurrentUser()!=null;
    }


    public void verifyPhoneNumber(String number, PhoneAuthProvider.OnVerificationStateChangedCallbacks onStateChange){
        Log.i("pttt", "Verifying " + number);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                callerActivity,               // Activity (for callback binding)
                onStateChange);        // OnVerificationStateChangedCallbacks

        currentState = LoginState.IN_PROGRESS;

    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential, OnCompleteListener<AuthResult> onSignIn) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(callerActivity,onSignIn);
    }


    //region Getters and Setters

    public String getAuthenticatedId(){
        if(firebaseAuth.getCurrentUser()!=null)
            return firebaseAuth.getCurrentUser().getUid();
        return null;
    }

    public void setAuthenticatedId(String id){
        this.authenticatedId = id;
    }

    public LoginState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(LoginState currentState) {
        this.currentState = currentState;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public String getVerificationId() {
        return mVerificationId;
    }

    public void setVerificationId(String mVerificationId) {
        this.mVerificationId = mVerificationId;
    }


    public Activity getCallerActivity() {
        return callerActivity;
    }

    //endregion



}
