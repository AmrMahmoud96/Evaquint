package com.evaquint.android.firebase.Authenticator;

import android.support.annotation.NonNull;
import android.util.Log;

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

import java.util.Arrays;

import static android.content.ContentValues.TAG;

/**
 * Created by henry on 8/22/2017.
 */

public class FacebookAuthenticator extends AuthenticationConnector {
    private CallbackManager mCallbackManager;

    public FacebookAuthenticator () throws Exception{
        super(FirebaseAuth.getInstance());
        this.fUser = mAuth.getCurrentUser();

    }

    private FirebaseAuth getAuth(){
        try {
            return FirebaseAuth.getInstance();
        } catch (Exception e) {
            Log.w(TAG, "FacebookAuthenticator Instantiation Failure" + e.getMessage());
            return null;
        }
    }

    public void initAuth() {
        mCallbackManager = CallbackManager.Factory.create();
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
                        // ...
                    }
                });
    }

    public void onExecute(){
        LoginManager.getInstance().logInWithReadPermissions(
                activity,
                Arrays.asList("email", "public_profile", "user_friends")
        );
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
                            FacebookAuthenticator.super.updateUser(user);
//                            updateUI(user);
                        } else {
                            // If sign in fails, display activity message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Toast.makeText(FacebookLoginActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
