package com.evaquint.android.utils.authenticator;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.evaquint.android.utils.dataStructures.UserDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static android.content.ContentValues.TAG;
import static com.evaquint.android.utils.database.DBUserInterface.addUser;

/**
 * Created by henry on 8/23/2017.
 */

public class EmailAuthenticator implements FirebaseAuthenticator {
    private FirebaseAuth mAuth;
    private Activity activity;
    private Intent nextActivity;
    private UserDB user;

    public EmailAuthenticator(){
        this.mAuth = FirebaseAuth.getInstance();
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

    public void createAccount(final String name, final String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
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
                            addUser(fUser.getUid(),
                                    new UserDB(fUser.getProviders(),
                                            name,
                                            email,
                                            "6476731234"));
                            activity.startActivity(nextActivity);
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                        // ...
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
