package com.evaquint.android.firebase.Authenticator;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.evaquint.android.LoginActivity;
import com.evaquint.android.R;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.content.ContentValues.TAG;
import static com.evaquint.android.firebase.FirebaseDBHandler.updateUser;
import static com.evaquint.android.utils.IntentValues.GOOGLE_SIGN_IN;

/**
 * Created by henry on 8/22/2017.
 */

public class GoogleAuthenticator implements FirebaseAuthenticator {
    private FirebaseUser fUser;
    private FirebaseAuth mAuth;
    private Activity activity;
    private Fragment fragment;
    private Intent nextActivity;
    private GoogleApiClient mGoogleApiClient;

    public GoogleAuthenticator(){
        this.mAuth = FirebaseAuth.getInstance();
        this.fUser = mAuth.getCurrentUser();
    }

    public GoogleAuthenticator(Activity a, Intent intent){
        this();
        this.activity = a;
        this.nextActivity=intent;
    }

    public GoogleAuthenticator(Fragment f, Intent intent){
        this(f.getActivity(), intent);
        this.fragment = f;
    }

    @Override
    public void initAuth() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
//                .enableAutoManage(this /* FragmentActivity */, null)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void startAuth() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        if (fragment==null)
            activity.startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        else
            fragment.startActivityForResult(signInIntent, GOOGLE_SIGN_IN);

    }

    @Override
    public void executeAuth() {
        initAuth();
        startAuth();
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            final GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
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
                                Toast.makeText(activity, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            }

                            // ...
                        }
                    });
        }
    }

    @Override
    public void updateEmail() {

    }
}
