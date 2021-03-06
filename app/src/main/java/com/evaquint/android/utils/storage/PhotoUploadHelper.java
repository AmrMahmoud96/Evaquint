package com.evaquint.android.utils.storage;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;

import static com.evaquint.android.utils.code.DatabaseValues.EVENTS_TABLE;
import static com.evaquint.android.utils.code.DatabaseValues.USER_TABLE;
/**
 * Created by henry on 2/9/2018.
 */

public class PhotoUploadHelper {
    FirebaseStorage storage;
    StorageReference storageRef;
    public PhotoUploadHelper(){
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("images");
    }
    public void uploadUserImage(String userID){
        storageRef.child(USER_TABLE.getName()).child(userID +  ".jpg");

    }
    public void uploadEventImage(String eventID, Uri filePath){
       StorageReference ref =  storageRef.child(EVENTS_TABLE.getName()).child(eventID).child((new File(filePath.toString())).getName());
        if(filePath != null)
        {

//            final ProgressDialog progressDialog = new ProgressDialog(this);
//             progressDialog.setTitle("Uploading...");
//            progressDialog.show();
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            progressDialog.dismiss();
//                            Toast.makeText(HomeActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            Log.d("Image upload", "Success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            Toast.makeText(HomeActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("Image upload", "fail");
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            Log.d("progress", ""+ progress);
//                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
    public void uploadUserProfileImage(final String userID, final Uri filePath){
        StorageReference ref =  storageRef.child(USER_TABLE.getName()).child(userID).child(""+0);
        if(filePath != null)
        {
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(userID).child("picture");
                            ref.setValue(taskSnapshot.getDownloadUrl().toString());
                            Log.d("Image upload", "Success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            Toast.makeText(HomeActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("Image upload", "fail");
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            Log.d("progress", ""+ progress);
//                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
    public void uploadEventImageAt(final String eventID, final Uri filePath,final int index){
        StorageReference ref =  storageRef.child(EVENTS_TABLE.getName()).child(eventID).child(""+index);
        if(filePath != null)
        {

//            final ProgressDialog progressDialog = new ProgressDialog(this);
//             progressDialog.setTitle("Uploading...");
//            progressDialog.show();
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(EVENTS_TABLE.getName()).child(eventID).child("details").child("pictures");
                            ref.child(""+index).setValue(taskSnapshot.getDownloadUrl().toString());
//                            progressDialog.dismiss();
//                            Toast.makeText(HomeActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            Log.d("Image upload", "Success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            Toast.makeText(HomeActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("Image upload", "fail");
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            Log.d("progress", ""+ progress);
//                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    public StorageReference getStorageRef(){
        return storageRef;
    }

    public void uploadEventImage(String eventID, FileInputStream rawData){
        storageRef.child(EVENTS_TABLE.getName()).child(eventID).putStream(rawData);
    }
}
