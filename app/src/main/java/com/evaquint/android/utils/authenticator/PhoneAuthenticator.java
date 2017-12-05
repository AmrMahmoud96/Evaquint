//package com.evaquint.android.utils.authenticator;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.evaquint.android.utils.dataStructures.UserDB;
//import com.evaquint.android.utils.database.UserDBHelper;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.PhoneAuthProvider;
//import com.google.firebase.auth.UserProfileChangeRequest;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.concurrent.TimeUnit;
//
//import static android.content.ContentValues.TAG;
//
///*
// * Created by Amr on 12/03/2017.
// */
//
//public class PhoneAuthenticator implements FirebaseAuthenticator {
//    private FirebaseAuth mAuth;
//    private Activity activity;
//    private Intent nextActivity;
//    private UserDBHelper userDatabaseHandler;
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
//    SimpleDateFormat df;
//
//    public PhoneAuthenticator(){
//        this.mAuth = FirebaseAuth.getInstance();
//        this.userDatabaseHandler=new UserDBHelper();
//        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    }
//
//    public PhoneAuthenticator(Activity a, Intent intent){
//        this();
//        this.activity = a;
//        this.nextActivity = intent;
//    }
//
//    @Override
//    public void executeAuth() {
//
//    }
//
//
//    public void createPAccount(String phoneNumber){
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60, TimeUnit.SECONDS,this.activity,mCallbacks);
//
//    }
//
//    @Override
//    public void handleResult(int requestCode, int resultCode, Intent data) {
//
//    }
//
//    @Override
//    public void updateEmail() {
//
//    }
//}
