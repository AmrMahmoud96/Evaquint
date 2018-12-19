package com.evaquint.android.fragments.signup;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.evaquint.android.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;

public class SignupMethodFrag extends Fragment {
    private String phoneNumber;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private EditText mPhoneNumberField;

    private View view;
    private Activity activity;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.view = inflater.inflate(R.layout.fragment_signup_method, container, false);
        this.activity = getActivity();

        ((Button) view.findViewById(R.id.signup_method_next_button)).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateValues()){
                    sendVerificationCode();
                   // nextFrag();
                }
            }
        });
        mPhoneNumberField = view.findViewById(R.id.phoneNumberField);

        return this.view;
    }

    private boolean validateValues(){
        phoneNumber = mPhoneNumberField.getText().toString().trim();
        Log.i("phone:",phoneNumber);
        if(phoneNumber.isEmpty()){
            mPhoneNumberField.setError("Please enter your phone number.");
            mPhoneNumberField.requestFocus();
            return false;
        }
        return true;
    }
    private void sendVerificationCode(){
        if( PhoneAuthProvider.getInstance()!=null){
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    Log.d("cred", "onVerificationCompleted:" + credential);

                    //    signInWithPhoneAuthCredential(credential);
                /*private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        FirebaseUser user = task.getResult().getUser();
                        // ...
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                }
            });
}*/
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    Log.w("failed", "onVerificationFailed", e);

                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Log.i("invalid", e.getMessage());
                        // Invalid request
                        // ...
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        Log.i("quota reached", e.getMessage());
                        // The SMS quota for the project has been exceeded
                        // ...
                    }

                    // Show a message and update the UI
                    // ...
                }

                @Override
                public void onCodeSent(String verificationId,
                                       PhoneAuthProvider.ForceResendingToken token) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    Log.d("codesent", "onCodeSent:" + verificationId);

                    // Save verification ID and resending token so we can use them later
                    mVerificationId = verificationId;
                    mResendToken = token;

                    nextFrag();
                    //  PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

                    // ...
                }
            };

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this.activity,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks


        }else{
            Log.i("null provider","");
        }

    }

    private void nextFrag(){
        SignupVerificationFrag signupVerificationFrag = new SignupVerificationFrag();
        Bundle verificationData = new Bundle();
        verificationData.putString("phoneNumber",phoneNumber);
        verificationData.putString("verificationID",mVerificationId);
        verificationData.putParcelable("resendToken", mResendToken);
        signupVerificationFrag.setArguments(verificationData);
        setActiveFragment(SignupMethodFrag.this, signupVerificationFrag);
    }
}
