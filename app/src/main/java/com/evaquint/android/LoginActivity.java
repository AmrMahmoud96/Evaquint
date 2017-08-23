package com.evaquint.android;


import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.evaquint.android.Fragments.EventLocatorFrag;
import com.evaquint.android.Fragments.Login.SignInFrag;
import com.evaquint.android.firebase.Authenticator.FacebookAuthenticator;
import com.evaquint.android.firebase.Authenticator.FirebaseAuthenticator;
import com.evaquint.android.firebase.Authenticator.GoogleAuthenticator;
import com.facebook.CallbackManager;
import com.facebook.login.LoginFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import in.championswimmer.libsocialbuttons.buttons.BtnFacebook;
import in.championswimmer.libsocialbuttons.buttons.BtnGoogleplus;

import static android.Manifest.permission.READ_CONTACTS;
import static com.evaquint.android.utils.IntentValues.GOOGLE_SIGN_IN;
import static com.evaquint.android.utils.ViewAnimator.*;

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
     * TODO: remove after connecting to activity real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // UI references.
    private AutoCompleteTextView mEmailView;
    private Fragment activeFragment = null;
    private FirebaseAuth mAuth;
    //Google Sign-In
    private FirebaseAuthenticator authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_landing);

        mAuth = FirebaseAuth.getInstance();

        BtnGoogleplus mGoogleSignInButton = (BtnGoogleplus) findViewById(R.id.login_google);
        mGoogleSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                authManager = new GoogleAuthenticator(LoginActivity.this,
                        new Intent(LoginActivity.this, MainActivity.class));
                authManager.executeAuth();
            }
        });

        BtnFacebook mFacebookSignInButton = (BtnFacebook) findViewById(R.id.login_facebook);
        mFacebookSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                authManager = new FacebookAuthenticator(LoginActivity.this,
                        new Intent(LoginActivity.this, MainActivity.class));
                authManager.executeAuth();
            }
        });

        TextView loginButton = (TextView) findViewById(R.id.to_login);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setActiveFragment(new SignInFrag());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
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

//
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when activity permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                populateAutoComplete();
            }
        }
    }

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
            authManager.handleResult(requestCode, resultCode, data);
        } else {
            authManager.handleResult(requestCode, resultCode, data);
        }
    }

}

