package com.evaquint.android.firebase.authenticator;

import android.content.Intent;

/**
 * Created by henry on 8/22/2017.
 */

public interface FirebaseAuthenticator {

    public void initAuth();
    public void startAuth();
    public void executeAuth();
    public void handleResult(int requestCode, int resultCode, Intent data) ;
    public void updateEmail();

}
