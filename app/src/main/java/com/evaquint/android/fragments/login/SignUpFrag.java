package com.evaquint.android.fragments.login;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    private ProgressBar mProgressBar;

    private Button mRegisterBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDataBase;

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
       // mProgress = new ProgressBar(activity, null,android.R.attr.progressBarStyleSmall)
        mRegisterBtn = (Button) view.findViewById(R.id.registerBtn);
        mProgressBar = (ProgressBar) view.findViewById(R.id.signUpProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
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
        mProgressBar.setVisibility(View.VISIBLE);
        final String firstName = mFirstNameField.getText().toString().trim();
        final String lastName = mLastNameField.getText().toString().trim();
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

        EmailAuthenticator emailAuthenticator = new EmailAuthenticator(activity,
                new Intent(activity, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));

        emailAuthenticator.createAccount(firstName,lastName, email, password);
        mProgressBar.setVisibility(View.INVISIBLE);
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