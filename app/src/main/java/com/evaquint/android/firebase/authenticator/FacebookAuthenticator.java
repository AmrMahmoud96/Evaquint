package com.evaquint.android.firebase.authenticator;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import static android.content.ContentValues.TAG;
import static com.evaquint.android.firebase.FirebaseDBHandler.*;

/**
 * Created by henry on 8/22/2017.
 */

public class FacebookAuthenticator implements FirebaseAuthenticator {
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private Activity activity;
    private Fragment fragment;
    private Intent nextActivity;

    public FacebookAuthenticator(){
        this.mAuth = FirebaseAuth.getInstance();
        this.mCallbackManager = CallbackManager.Factory.create();
    }

    public FacebookAuthenticator(Activity a, Intent intent){
        this();
        this.activity = a;
        this.nextActivity = intent;
    }

    public FacebookAuthenticator(Fragment f, Intent intent){
        this(f.getActivity(), intent);
        this.fragment = f;
    }


    public void initAuth() {
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                        Toast.makeText(activity, "Facebook Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void startAuth(){
        if (fragment==null)
            LoginManager.getInstance().logInWithReadPermissions(
                    activity,
                    Arrays.asList("email", "public_profile", "user_friends")
            );
        else
            LoginManager.getInstance().logInWithReadPermissions(
                    fragment,
                    Arrays.asList("email", "public_profile", "user_friends")
            );
    }

    @Override
    public void executeAuth() {
        initAuth();
        startAuth();
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void updateEmail() {

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            com.google.firebase.auth.FirebaseUser user = mAuth.getCurrentUser();
                            updateUser(user);
                            activity.startActivity(nextActivity);
//                            updateUI(user);
                        } else {
                            // If sign in fails, display activity message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(activity, "Facebook Authentication failed(1).",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
