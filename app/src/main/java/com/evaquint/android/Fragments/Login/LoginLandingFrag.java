package com.evaquint.android.Fragments.Login;

/**
 * Created by henry on 8/22/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evaquint.android.MainActivity;
import com.evaquint.android.R;
import com.evaquint.android.firebase.Authenticator.FacebookAuthenticator;
import com.evaquint.android.firebase.Authenticator.FirebaseAuthenticator;
import com.evaquint.android.firebase.Authenticator.GoogleAuthenticator;

import com.google.firebase.auth.FirebaseAuth;

import in.championswimmer.libsocialbuttons.buttons.BtnFacebook;
import in.championswimmer.libsocialbuttons.buttons.BtnGoogleplus;

import static com.evaquint.android.utils.FragmentHelper.setActiveFragment;
import static com.evaquint.android.utils.IntentValues.GOOGLE_SIGN_IN;

/**
 * Created by henry on 8/22/2017.
 */

public class LoginLandingFrag extends Fragment {
    private View view;
    private Activity activity;
    private FirebaseAuth mAuth;
    private FirebaseAuthenticator authManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.view =inflater.inflate(R.layout.fragment_login_landing, container, false);
        this.activity =getActivity();

        mAuth = FirebaseAuth.getInstance();

        BtnGoogleplus mGoogleSignInButton = (BtnGoogleplus) view.findViewById(R.id.login_google);
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authManager = new GoogleAuthenticator(LoginLandingFrag.this,
                        new Intent(activity, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                authManager.executeAuth();
            }
        });

        BtnFacebook mFacebookSignInButton = (BtnFacebook) view.findViewById(R.id.login_facebook);
        mFacebookSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authManager = new FacebookAuthenticator(LoginLandingFrag.this,
                        new Intent(activity, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                authManager.executeAuth();
            }
        });

        TextView loginButton = (TextView) view.findViewById(R.id.to_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActiveFragment(LoginLandingFrag.this, new SignInFrag());
            }
        });

        return this.view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            authManager.handleResult(requestCode, resultCode, data);
        } else {
            authManager.handleResult(requestCode, resultCode, data);
        }
    }

}
