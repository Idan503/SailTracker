package com.idan_koren_israeli.sailtracker.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.idan_koren_israeli.sailtracker.ClubMember;
import com.idan_koren_israeli.sailtracker.OnLoginCompleteListener;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.common.FirestoreManager;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 *  This class purpose is just to log in and authenticate the user via its phone number
 *  an authenticated user FirebaseUser object will be generated and sent back via callback.
 *
 */

// Users can Login with phone number, authentication via SMS like WhatsApp
public class LoginFragment extends Fragment {
    private enum LoginState{ NOT_STARTED,IN_PROGRESS, CODE_SENT, CODE_APPROVED, COMPLETED, FAILED}
    private interface KEYS {
        String LOGIN_STATE = "LOGIN_STATE";
        String LOGIN_PHONE = "LOGIN_PHONE";
    }

    private LoginState currentState = LoginState.NOT_STARTED;
    private String currentPhone;

    private Button nextButton;
    private EditText phoneEditText, codeEditText, nameEditText;
    private TextView messageText;

    private OnLoginCompleteListener finishedListener;


    private FirebaseAuth auth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseUser loggedUser;



    public LoginFragment() {
        // Required empty public constructor
    }


    public void setOnCompleteListener(OnLoginCompleteListener listener){
        finishedListener = listener;
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
            currentState = (LoginState) savedInstanceState.getSerializable(KEYS.LOGIN_STATE);
            currentPhone = savedInstanceState.getString(KEYS.LOGIN_PHONE);

            if(currentState == LoginState.CODE_SENT)
                showAuthCodeLayout();
            else if (currentState == LoginState.IN_PROGRESS)
                verifyPhoneNumber();

        }

        auth = FirebaseAuth.getInstance();
        auth.useAppLanguage();

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
        messageText = root.findViewById(R.id.login_LBL_message);
        codeEditText = root.findViewById(R.id.login_EDT_code_box);
        nameEditText = root.findViewById(R.id.login_EDT_name_box);

    }

    private void setListeners(){
        nextButton.setOnClickListener(nextButtonListener);
    }

    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (currentState){
                case CODE_SENT:
                    // Code user entered the code, we will check if it is correct
                    String code = codeEditText.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                    signInWithPhoneAuthCredential(credential);
                    break;
                case CODE_APPROVED:
                    // User type his profile name, setting it to the current logged profile
                    if(loggedUser!=null && loggedUser.getDisplayName()==null){
                        loggedUser.updateProfile(buildNewProfile());
                        currentState = LoginState.COMPLETED;
                        CommonUtils.getInstance().showToast("Logged in successfully");


                        // Fragment can now be finished
                        finishedListener.onLoginFinished(loggedUser);
                    }
                    break;

                default:
                    verifyPhoneNumber();
                    break;
            }
        }
    };


    private void verifyPhoneNumber(){
        if(phoneEditText.getText()!=null)
            currentPhone = phoneEditText.getText().toString();


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                currentPhone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                Objects.requireNonNull(getActivity()),               // Activity (for callback binding)
                callbacks);        // OnVerificationStateChangedCallbacks

        currentState = LoginState.IN_PROGRESS;

    }

    // This needs to be improved
    private boolean isValidNumber(String number){
        return number.length()>9 && number.length()<20;
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            CommonUtils.getInstance().showToast("Complete!");
            signInWithPhoneAuthCredential(phoneAuthCredential);

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            CommonUtils.getInstance().showToast("Failed!");
            currentState = LoginState.FAILED;

            if(e instanceof FirebaseAuthInvalidCredentialsException){
                CommonUtils.getInstance().showToast("Error - Invalid request");
            } else if(e instanceof FirebaseTooManyRequestsException){
                CommonUtils.getInstance().showToast("Too many requests");
            }
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            CommonUtils.getInstance().showToast("Code Sent!");
            mVerificationId = verificationId;
            mResendToken = token;
            currentState = LoginState.CODE_SENT;

            showAuthCodeLayout();
        }
    };


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            currentState = LoginState.CODE_APPROVED;
                            loggedUser = Objects.requireNonNull(task.getResult()).getUser();
                            showNewProfileLayout();
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                CommonUtils.getInstance().showToast("Invalid code, please try again");
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private UserProfileChangeRequest buildNewProfile(){
        UserProfileChangeRequest.Builder profileBuilder = new UserProfileChangeRequest.Builder();
        profileBuilder.setDisplayName(nameEditText.getText().toString());
        profileBuilder.setPhotoUri(Uri.parse("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png"));
        return profileBuilder.build();
    }

    private void showNewProfileLayout(){
        codeEditText.setVisibility(View.GONE);
        phoneEditText.setVisibility(View.GONE);
        nameEditText.setVisibility(View.VISIBLE);
        messageText.setText(getResources().getString(R.string.login_name));
    }

    private void showAuthCodeLayout(){
        codeEditText.setVisibility(View.VISIBLE);
        phoneEditText.setVisibility(View.GONE);
        nameEditText.setVisibility(View.GONE);
        messageText.setText(getResources().getString(R.string.login_code));
    }

    @Override
    public void onSaveInstanceState(Bundle instanceBundle) {
        instanceBundle.putSerializable(KEYS.LOGIN_STATE, currentState);
        instanceBundle.putString(KEYS.LOGIN_PHONE, phoneEditText.getText().toString());
        super.onSaveInstanceState(instanceBundle);
    }
}