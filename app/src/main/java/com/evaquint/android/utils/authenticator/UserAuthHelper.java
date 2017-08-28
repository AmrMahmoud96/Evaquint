package com.evaquint.android.utils.authenticator;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.List;

/**
 * Created by henry on 8/24/2017.
 */

public class UserAuthHelper {
    private FirebaseUser user;
    private Activity activity;

    public UserAuthHelper (Activity activity){
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.activity = activity;
    }

    public void updateEmail(String email){
        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(activity, "Email Updated",
                                    Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(activity, "Update Failed",
                                    Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateName(UserProfileChangeRequest profileUpdates){
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "User profile updated",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void updatePassword(String password){
        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Password Updated",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void linkAccount(AuthCredential credential){
        user.linkWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(activity, "linkWithCredential success",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity,"linking failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }

    public void removeLink(String providerId){
        FirebaseAuth.getInstance().getCurrentUser().unlink(providerId)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(activity, "unlink success",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity,"unlinking failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void deleteUser(){
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "User Account Deleted",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
