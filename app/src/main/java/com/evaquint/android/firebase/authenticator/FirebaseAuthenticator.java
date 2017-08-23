package com.evaquint.android.firebase.authenticator;

import android.content.Intent;

/**
 * Created by henry on 8/22/2017.
 */

public interface FirebaseAuthenticator {

    void executeAuth();
    void handleResult(int requestCode, int resultCode, Intent data) ;
    void updateEmail();

}
