package com.evaquint.android.fragments.signup;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.evaquint.android.R;
import com.evaquint.android.utils.database.UserDBHelper;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import static com.evaquint.android.utils.code.DatabaseValues.USER_TABLE;
import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;

public class SignupVerificationFrag extends Fragment {
    private TextView mResendCodeText;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private EditText mVerificationCodeField;

    private String mPhoneNumer;
    private String mVerificationID;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private FirebaseAuth mAuth;


    private View view;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.view = inflater.inflate(R.layout.fragment_signup_verification, container, false);
        this.activity = getActivity();

        ((Button) view.findViewById(R.id.signup_verification_next_button)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       validateValues();
                    }
                });
        mVerificationCodeField = view.findViewById(R.id.verificationCodeField);
        mResendCodeText = view.findViewById(R.id.resendCodeText);

        mResendCodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //resend code
                resendVerificationCode(mPhoneNumer,mResendToken);
               // Log.i("id:",mVerificationID);
            }
        });
        mPhoneNumer = getArguments().getString("phoneNumber");
        mVerificationID = getArguments().getString("verificationID");
        mResendToken = getArguments().getParcelable("resendToken");
        mAuth = FirebaseAuth.getInstance();
        ((TextView)view.findViewById(R.id.verificationCodePageText)).setText("Please enter the verification code sent to "+mPhoneNumer+" in the box below");
        //Log.i("id:",mVerificationID);
        //Log.i("resendToken",mResendToken.toString());
        return this.view;
    }
    //    signInWithPhoneAuthCredential(credential);
                private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.i("status", "signInWithCredential:success");

                        FirebaseUser user = task.getResult().getUser();
                        UserDBHelper userDBHelper = new UserDBHelper();
                        String userID = user.getUid();

                        if(!userID.isEmpty()&&userID!=null){
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(userID);
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                                        Log.i("datasnapshot", dataSnapshot.toString());
                                        Log.i("result;","user in database already exists");
                                    }
                                    else{
                                        Log.i("result;","user in database doesn't exist");
                                        nextFrag();
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w("error: ", "onCancelled", databaseError.toException());

                                }
                            });
                        }
                        Log.i("userid", user.getUid());
                        Log.i("user", user.toString());

                        // ...
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.i("status", "signInWithCredential:failure", task.getException());
                        mVerificationCodeField.setError("Verification code incorrect. Please try again.");
                        mVerificationCodeField.requestFocus();
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                }
            });
}

    private void validateValues(){
        String verificationCode = mVerificationCodeField.getText().toString();
        if(verificationCode!=null && !verificationCode.isEmpty() &&verificationCode.length()==6){
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationID, verificationCode);
            signInWithPhoneAuthCredential(credential);
        }else{
            mVerificationCodeField.setError("Please enter the 6-digit verification code sent to your phone number.");
            mVerificationCodeField.requestFocus();
        }
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
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

                        signInWithPhoneAuthCredential(credential);
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
                    mVerificationID = verificationId;
                    mResendToken = token;

                    // ...
                }
            };

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this.activity,               // Activity (for callback binding)
                    mCallbacks,         // OnVerificationStateChangedCallbacks
                    token);         //Resend token


        }else{
            Log.i("null provider","");
        }
    }
    private void nextFrag(){
        setActiveFragment(SignupVerificationFrag.this, new SignupInformationFrag());
    }
}

