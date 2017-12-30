package com.evaquint.android.fragments.login;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.evaquint.android.MainActivity;
import com.evaquint.android.R;
import com.evaquint.android.utils.authenticator.EmailAuthenticator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;

/**
 * Created by henry on 11/5/2017.
 */

public class SignUpFrag extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private View view;
    private Activity activity;
    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPasswordField;
    private ProgressBar mProgressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    private Button mRegisterBtn;

   // private ProgressBar mProgress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.view = inflater.inflate(R.layout.fragment_register, container, false);
        this.activity = getActivity();

     //   mAuth = FirebaseAuth.getInstance();

       // mDataBase = FirebaseDatabase.getInstance().getReference().child("Users");

        mFirstNameField = (EditText) view.findViewById(R.id.firstNameField);
        mLastNameField = (EditText) view.findViewById(R.id.lastNameField);
        mEmailField =(EditText) view.findViewById(R.id.emailField);
        mPasswordField = (EditText) view.findViewById(R.id.passwordField);
        mConfirmPasswordField = (EditText) view.findViewById(R.id.confirmPasswordField);

       // mProgress = new ProgressBar(activity, null,android.R.attr.progressBarStyleSmall)
        mRegisterBtn = (Button) view.findViewById(R.id.registerBtn);
        mProgressBar = (ProgressBar) view.findViewById(R.id.signUpProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

//        // Start long running operation in a background thread
//        new Thread(new Runnable() {
//            public void run() {
//                while (progressStatus < 100) {
//                    progressStatus += 1;
//                    // Update the progress bar and display the
//                    //current value in the text view
//                    handler.post(new Runnable() {
//                        public void run() {
//                            mProgressBar.setProgress(progressStatus);
//                        }
//                    });
//                    try {
//                        // Sleep for 200 milliseconds.
//                        Thread.sleep(200);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();

        final Button mSwitchButton = (Button) view.findViewById(R.id.switch_button);
        mSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActiveFragment(SignUpFrag.this, new SignInFrag());
            }
        });
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();

            }
        });
        // Set up everything else


        return this.view;
    }

    public void startRegister(){
        final String firstName = mFirstNameField.getText().toString().trim();
        final String lastName = mLastNameField.getText().toString().trim();
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();
        String confirmPassword= mConfirmPasswordField.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;


//        if(!password.equals(confirmPassword)){
//            mConfirmPasswordField.setError("Passwords are not the same.");
//            focusView = mPasswordField;
//            cancel = true;
//        }
//        if (password.length()<8 || !password.matches(".*\\d+.*")) {
//            mPasswordField.setError(getString(R.string.password_requirements));
//            focusView = mPasswordField;
//            cancel = true;
//        }
//        if(TextUtils.isEmpty(email)||!email.contains("@")||!email.contains(".")){
//            mEmailField.setError(getString(R.string.error_invalid_email));
//            focusView=mEmailField;
//            cancel=true;
//        }
//        if(TextUtils.isEmpty(firstName)||firstName.matches(".*\\d+.*")){
//            mFirstNameField.setError(getString(R.string.error_invalid_name));
//            focusView = mFirstNameField;
//            cancel = true;
//        }
//        if(TextUtils.isEmpty(lastName)||lastName.matches(".*\\d+.*")){
//            mLastNameField.setError(getString(R.string.error_invalid_name));
//            focusView=mLastNameField;
//            cancel = true;
//        }


        if (password.length()<1) {
            mPasswordField.setError("Password needs to be longer");
            focusView = mPasswordField;
            cancel = true;
        }

        if(TextUtils.isEmpty(email)||!email.contains("@")||!email.contains(".")){
            mEmailField.setError(getString(R.string.error_invalid_email));
            focusView=mEmailField;
            cancel=true;
        }


        if(cancel){
            focusView.requestFocus();
        }else{
            handler.post(new Runnable() {
                public void run() {
                    mProgressBar.setVisibility(View.VISIBLE);
                    SignUpFrag.this.getView().setAlpha( (float) 0.5 );
                }
            });

            EmailAuthenticator emailAuthenticator = new EmailAuthenticator(activity,
                    new Intent(activity, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));

            emailAuthenticator.createAccount(firstName,lastName, email, password);

        }

        handler.post(new Runnable() {
            public void run() {
                SignUpFrag.this.getView().setAlpha(1);
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });


//        if(!TextUtils.isEmpty(name)&& !TextUtils.isEmpty(email)&& !TextUtils.isEmpty(password)){
//
//
//            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//                    if(task.isSuccessful()){
//
//                        String user_id = mAuth.getCurrentUser().getUid();
//
//                       DatabaseReference current_user_db = mDataBase.child(user_id);
//
//                        current_user_db.child("name").setValue(name);
//                        current_user_db.child("image").setValue("default");
//                        Intent mainIntent = new Intent(activity, MainActivity.class);
//                       startActivity(mainIntent);
//
//                    }
//
//                }
//            });
//
//        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}