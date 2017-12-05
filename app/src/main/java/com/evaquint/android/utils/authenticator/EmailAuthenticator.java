package com.evaquint.android.utils.authenticator;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.evaquint.android.utils.dataStructures.UserDB;
import com.evaquint.android.utils.database.UserDBHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

/**
 * Created by henry on 8/23/2017.
 */

public class EmailAuthenticator implements FirebaseAuthenticator {
    private FirebaseAuth mAuth;
    private Activity activity;
    private Intent nextActivity;
    private UserDBHelper userDatabaseHandler;
    SimpleDateFormat df;

    public EmailAuthenticator(){
        this.mAuth = FirebaseAuth.getInstance();
        this.userDatabaseHandler=new UserDBHelper();
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public EmailAuthenticator(Activity a, Intent intent){
        this();
        this.activity = a;
        this.nextActivity = intent;
    }

    @Override
    public void executeAuth() {

    }

    public void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");

                           // checkIfEmailVerified();

                            activity.startActivity(nextActivity);

//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
    private void checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
            // user is verified, so you can finish this activity or send user to activity which you want.
           // finish();
           // Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();

        }
        else
        {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(activity,"Please verify your email and try again.",Toast.LENGTH_SHORT).show();
            //restart this activity

        }
    }

    public void createAccount(final String firstName,final String lastName, final String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String name = firstName +" "+ lastName;
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser fUser = mAuth.getCurrentUser();
                            UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            fUser.updateProfile(updateProfile).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    activity.startActivity(nextActivity);
                                }
                            });
                         //   userDatabaseHandler.addUser(fUser.getUid(),new UserDB());
                            userDatabaseHandler.addUser(fUser.getUid(),
                                    new UserDB(firstName,lastName,"default",email,"6476731234", new ArrayList<String>(),df.format(Calendar.getInstance().getTime())));
                          //  sendVerificationEmail();
                            activity.startActivity(nextActivity);
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Log.w(TAG,"Message: "+task.getException().getMessage());
                            Log.w(TAG, "Class: "+task.getException().getClass());
                          //  Log.w(TAG,"equals: "+);
                            if(task.getException().getClass().equals(FirebaseAuthUserCollisionException.class)){
                               // boolean equals = task.getException().getClass().equals(FirebaseAuthUserCollisionException);
                               Toast.makeText(activity,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                          /*  else if(){

                            }*/
                           else{
                                Toast.makeText(activity, "Sign Up failed.",
                                    Toast.LENGTH_SHORT).show();
                            }
//                            updateUI(null);
                        }
                        // ...
                    }
                });
    }

    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent


                            // after email is sent just logout the user and finish this activity
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(activity, "Verification email sent.", Toast.LENGTH_SHORT).show();
                           // startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                          //  finish();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            //restart this activity
                            Toast.makeText(activity, "Email not sent.", Toast.LENGTH_SHORT).show();
//                            overridePendingTransition(0, 0);
//                            finish();
//                            overridePendingTransition(0, 0);
//                            startActivity(getIntent());

                        }
                    }
                });
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void updateEmail() {

    }
}
