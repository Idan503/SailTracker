package com.idan_koren_israeli.sailtracker.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.common.SharedPrefsManager;
import com.idan_koren_israeli.sailtracker.firebase.LoginManager;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnLoginFinishedListener;
import com.idan_koren_israeli.sailtracker.firebase.callbacks.OnMemberLoadListener;

import java.util.InputMismatchException;


/**
 *  This class purpose is just to log in and authenticate the user via its phone number
 *  an authenticated user FirebaseUser object will be generated and sent back via callback.
 *
 */

// Users can Login with phone number, authentication via SMS like WhatsApp
public class LoginFragment extends Fragment {

    private interface KEYS {
        String LOGIN_PHONE = "LOGIN_PHONE";
    }

    private String currentPhone;
    private LoadingFragment loadingFragment;

    private Button nextButton, backButton;
    private EditText phoneEditText, codeEditText, nameEditText;
    private ViewFlipper viewFlipper;

    private OnLoginFinishedListener finishedListener;

    private LoginManager manager;
    private SharedPrefsManager sp;


    public LoginFragment() {
        // Required empty public constructor
    }


    public void setOnCompleteListener(OnLoginFinishedListener listener){
        finishedListener = listener;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = SharedPrefsManager.getInstance();

        if(getActivity()!=null) {
            manager = LoginManager.getInstance(getActivity());
        }

        if(savedInstanceState!=null) {
            currentPhone = savedInstanceState.getString(KEYS.LOGIN_PHONE);

            if(manager.getCurrentState() == LoginManager.LoginState.CODE_SENT)
                viewFlipper.setDisplayedChild(1); // showing code layout
            else if (manager.getCurrentState() == LoginManager.LoginState.IN_PROGRESS)
                manager.verifyPhoneNumber(currentPhone, onVerificationStateChanged);


        }
    }

    @Override
    public void onStart() {
        super.onStart();

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
        backButton =  root.findViewById(R.id.login_BTN_back);
        phoneEditText = root.findViewById(R.id.login_EDT_phone_box);
        viewFlipper = root.findViewById(R.id.login_FLIP_flipper);
        codeEditText = root.findViewById(R.id.login_EDT_code);
        nameEditText = root.findViewById(R.id.login_EDT_name);
        loadingFragment = (LoadingFragment) getChildFragmentManager().findFragmentById(R.id.login_FRAG_loading);

    }

    private void setListeners(){
        nextButton.setOnClickListener(nextButtonListener);
        backButton.setOnClickListener(backButtonListener);
    }


    //region Next & Back listeners
    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (manager.getCurrentState()){
                case CODE_SENT: // Code Screen
                    // Code user entered the code, we will check if it is correct
                    checkUserCode();
                    backButton.setVisibility(View.GONE);
                    break;
                case CODE_APPROVED: // Display Name Screen
                    // User type his profile name, setting it to the current logged profile
                    generateMember();
                    backButton.setVisibility(View.GONE);
                    break;
                case NOT_STARTED: // Phone number screen
                    currentPhone = CommonUtils.getInstance().toPhoneString(phoneEditText.getText().toString());
                    verifyUserPhoneNumber();
                    loadingFragment.show();

                    break;
            }
        }
    };

    private View.OnClickListener backButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showPreviousLayout();
        }
    };

    //endregion

    //region Authentication Steps Methods
    // Using input number to connect to firebase auth server
    private void verifyUserPhoneNumber(){
        if(!phoneEditText.getText().toString().matches("")) {
            try {
                manager.verifyPhoneNumber(currentPhone, onVerificationStateChanged);
            }
            catch (InputMismatchException err){
                CommonUtils.getInstance().showToast("Please insert a valid phone number");
                err.printStackTrace();
            }
        }
        else{
            CommonUtils.getInstance().showToast("Please insert a phone number");
        }
    }

    // Using the verification code that user enters to check for validation
    private void checkUserCode(){
        String code = codeEditText.getText().toString();
        if(!code.matches("")) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(manager.getVerificationId(), code);
            manager.signInWithPhoneAuthCredential(credential, onSignInCompleted);
            backButton.setEnabled(false);
        }
        else
        {
            CommonUtils.getInstance().showToast("Please insert a valid code");
        }
    }

    // Using the input name of the new connected user to generate member object and send it to the callback
    private void generateMember(){
        if(manager.getAuthenticatedId()!=null){
            if(!nameEditText.getText().toString().matches("")) {
                // Creating the new member based on information from inside the fragment
                ClubMember loggedMember = new ClubMember(manager.getAuthenticatedId(), nameEditText.getText().toString(), currentPhone);

                manager.setCurrentState(LoginManager.LoginState.COMPLETED);
                CommonUtils.getInstance().showToast("Logged in successfully");

                // Fragment can now be finished
                finishedListener.onLoginFinished(loggedMember);
            }
            else
                CommonUtils.getInstance().showToast("Please insert name");
        }
    }

    //endregion

    //region Layout Flipper
    private void showNextLayout(){
        viewFlipper.setInAnimation(getContext(), R.anim.slide_in_right);
        viewFlipper.setOutAnimation(getContext(), R.anim.slide_out_left);
        viewFlipper.showNext();
        if(viewFlipper.getDisplayedChild()==1)
        {
            backButton.setVisibility(View.VISIBLE);
        }
        else
            backButton.setVisibility(View.GONE);

    }

    private void showPreviousLayout(){
        viewFlipper.setInAnimation(getContext(), android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(getContext(), android.R.anim.slide_out_right);
        viewFlipper.showPrevious();
        if(viewFlipper.getDisplayedChild()==0)
            backButton.setVisibility(View.GONE);
        else
            backButton.setVisibility(View.VISIBLE);
        manager.setCurrentState(LoginManager.LoginState.NOT_STARTED); // back to the start
    }

    //endregion


    @Override
    public void onSaveInstanceState(@NonNull Bundle instanceBundle) {
        if(!phoneEditText.getText().toString().matches("")) {
            currentPhone = CommonUtils.getInstance().toPhoneString(phoneEditText.getText().toString());
            instanceBundle.putString(KEYS.LOGIN_PHONE, currentPhone);
        }
        super.onSaveInstanceState(instanceBundle);
    }



    //region Authentication Callbacks
    private OnMemberLoadListener onMemberLoaded = new OnMemberLoadListener() {
        @Override
        public void onMemberLoad(ClubMember memberLoaded) {
            loadingFragment.hide();
            if(memberLoaded==null) {
                verifyUserPhoneNumber(); // New member -> new auth verify
            }
            else {

                finishedListener.onLoginFinished(memberLoaded);
            }

        }
    };


    private OnCompleteListener<AuthResult> onSignInCompleted =  new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                 manager.setCurrentState(LoginManager.LoginState.CODE_APPROVED);
                if(task.getResult()!=null && task.getResult().getUser()!=null)
                    manager.setAuthenticatedId(task.getResult().getUser().getUid());

                showNextLayout();
            } else {
                // Sign in failed, display a message and update the UI
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    CommonUtils.getInstance().showToast("Invalid code, please try again");
                    backButton.setEnabled(true);
                    backButton.setVisibility(View.VISIBLE);
                    // The verification code entered was invalid
                }
            }
        }
    };


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks onVerificationStateChanged = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            CommonUtils.getInstance().showToast("Complete!");
            manager.signInWithPhoneAuthCredential(phoneAuthCredential,onSignInCompleted);
            loadingFragment.hide();

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
             manager.setCurrentState(LoginManager.LoginState.NOT_STARTED);
             if(e.getMessage()!=null)
                Log.e(getClass().getSimpleName(), e.getMessage());
            if(e instanceof FirebaseAuthInvalidCredentialsException){
                CommonUtils.getInstance().showToast("Error - Invalid request");
            } else if(e instanceof FirebaseTooManyRequestsException){
                CommonUtils.getInstance().showToast("Too many requests");

            }
            else{
                CommonUtils.getInstance().showToast("Failed - Please try again later");
            }
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            CommonUtils.getInstance().showToast("Code Sent!");
            manager.setVerificationId(verificationId);
            manager.setCurrentState(LoginManager.LoginState.CODE_SENT);
            loadingFragment.hide();

            showNextLayout();
        }
    };

    //endregion


}