package com.evaquint.android;



import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.net.Uri;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;


import com.evaquint.android.firebase.DBConnector;
import com.evaquint.android.firebase.dataStructures.FirebaseUser;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

import in.championswimmer.libsocialbuttons.buttons.BtnFacebook;
import in.championswimmer.libsocialbuttons.buttons.BtnGoogleplus;


import static com.evaquint.android.utils.IntentValues.*;

import static android.content.ContentValues.TAG;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Fragment activeFragment = null;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    //Google Sign-In
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_landing);

        initGoogleLogin();
        mAuth = FirebaseAuth.getInstance();
        BtnGoogleplus mGoogleSignInButton = (BtnGoogleplus) findViewById(R.id.login_google);
        mGoogleSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
            }
        });

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

        BtnFacebook mFacebookSignInButton = (BtnFacebook) findViewById(R.id.login_facebook);
        mFacebookSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(
                        LoginActivity.this,
                        Arrays.asList("email", "public_profile", "user_friends")
                );
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        com.google.firebase.auth.FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }

    private void setActiveFragment(Fragment newFrag){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        if(activeFragment==null)
            ft.add(R.id.content_frame, newFrag);
        else
            ft.replace(activeFragment.getId(), newFrag);
//            loadFragment(ft);
        ft.commit();
        activeFragment=newFrag;
    }
    private void initGoogleLogin() {
        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this /* Context */)
//                .enableAutoManage(this /* FragmentActivity */, null)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

//
//    private boolean mayRequestContacts() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
//            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(android.R.string.ok, new View.OnClickListener() {
//                        @Override
//                        @TargetApi(Build.VERSION_CODES.M)
//                        public void onClick(View v) {
//                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//                        }
//                    });
//        } else {
//            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//        }
//        return false;
//    }
//
//    /**
//     * Callback received when a permissions request has been completed.
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_READ_CONTACTS) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                populateAutoComplete();
//            }
//        }
//    }



    /**
     * Shows the progress UI and hides the login form.
     */


    private void startApp() {
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
//        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
//            CacheManager test = new CacheManager(this);
////            SQLiteDatabase kekz = test.getWritableDatabase();
//            test.insertUser(USER_TABLE, acct);
//            String temp[] = test.retrieveData(USER_TABLE);

        }
    }

    private void updateFirebase(com.google.firebase.auth.FirebaseUser fUser){
        DBConnector test =new DBConnector("user");
        test.readFromDB("user");
        test.writeToDB(fUser.getUid(), new FirebaseUser(fUser.getProviders(),
                fUser.getDisplayName(), fUser.getEmail(), "6476731234"));
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            com.google.firebase.auth.FirebaseUser user = mAuth.getCurrentUser();
                            updateFirebase(user);
                            startApp();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            com.google.firebase.auth.FirebaseUser user = mAuth.getCurrentUser();
                            updateFirebase(user);
                            startApp();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
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

