package com.idan_koren_israeli.sailtracker.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.R;

import java.net.Authenticator;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


public class LoginFragment extends Fragment {

    boolean verificationInProgress = false;

    private Button nextButton;
    private EditText phoneEditText;
    private String currentPhone;

    private FirebaseAuth auth;


    public LoginFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null) {
            verificationInProgress = savedInstanceState.getBoolean("boolVerificationInProgress");
            currentPhone = savedInstanceState.getString("strCurrentPhone");
            if (verificationInProgress)
                verifyPhoneNumber();

            auth = FirebaseAuth.getInstance();
            auth.useAppLanguage();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent =inflater.inflate(R.layout.fragment_login, container, false);

        findViews(parent);
        setListeners();

        return parent;
    }

    private void findViews(View root){
        nextButton = root.findViewById(R.id.login_BTN_next);
        phoneEditText = root.findViewById(R.id.login_EDT_phone_box);
    }

    private void setListeners(){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPhoneNumber();
            }
        });

    }


    private void verifyPhoneNumber(){
        if(currentPhone==null)
            currentPhone = "+972" + phoneEditText.getText().toString().substring(1);

        if(!isValidNumber(currentPhone)){
            CommonUtils.getInstance().showToast("Phone number " + currentPhone +" is not valid");
            return;
        }


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                currentPhone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                Objects.requireNonNull(getActivity()),               // Activity (for callback binding)
                callbacks);        // OnVerificationStateChangedCallbacks



        verificationInProgress = true;

    }

    // This needs to be improved
    private boolean isValidNumber(String number){
        return number.length()>9 && number.length()<20;
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            CommonUtils.getInstance().showToast("Complete!");
            verificationInProgress = false;
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            CommonUtils.getInstance().showToast("Failed!");
            verificationInProgress = false;
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            CommonUtils.getInstance().showToast("Code Sent! " + currentPhone);
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                            if(user!=null && user.getPhoneNumber()!=null)
                                Log.i("pttt", user.getPhoneNumber());
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle instanceBundle) {
        instanceBundle.putBoolean("boolVerificationInProgress", verificationInProgress);
        instanceBundle.putString("strCurrentPhone", phoneEditText.getText().toString());
        super.onSaveInstanceState(instanceBundle);
    }
}