package com.evaquint.android.firebase.Authenticator;

import android.app.Activity;

import com.evaquint.android.firebase.DBConnector;
import com.evaquint.android.firebase.dataStructures.UserDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by henry on 8/22/2017.
 */

public class AuthenticationConnector {
    protected FirebaseUser fUser;
    protected FirebaseAuth mAuth;
    protected Activity activity;

    public AuthenticationConnector(){
        this.mAuth = FirebaseAuth.getInstance();
        this.fUser = mAuth.getCurrentUser();
    }

    public AuthenticationConnector(FirebaseAuth mAuth){
        this.mAuth = mAuth;
        this.fUser = mAuth.getCurrentUser();
    }

    public AuthenticationConnector(FirebaseAuth mAuth, Activity a){
        this(mAuth);
        this.activity = a;
    }

    public boolean isAuthenticated(){
        return fUser!=null;
    }

    public void initAuth(){

    }

    public void onExecute(){

    }



    public FirebaseUser getUser(){
        return fUser;
    }

    public void updateUser(FirebaseUser fUser){
        this.fUser=fUser;

        DBConnector test =new DBConnector("user");
        test.writeToDB(fUser.getUid(), new UserDB(fUser.getProviders(),
                fUser.getDisplayName(), fUser.getEmail(), "6476731234"));
    }

    public void updateEmail(){

    }

    protected boolean checkAuthStatus(){
        return mAuth.getCurrentUser()!=null;
    }

}
