package com.evaquint.android.utils.database;

import com.evaquint.android.utils.code.DatabaseValues;
import com.evaquint.android.utils.dataStructures.UserDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;

import static com.evaquint.android.utils.code.DatabaseValues.USER_TABLE;

/**
 * Created by henry on 8/22/2017.
 */

public class UserDBHelper {
    DBConnector dbConnector;

    public UserDBHelper() {
         dbConnector = new DBConnector(USER_TABLE);
    }

    public void addUser(FirebaseUser fUser) {
        dbConnector.writeToDB(fUser.getUid(), new UserDB(fUser.getProviders(),
                fUser.getDisplayName(), fUser.getEmail(), "6476731234"));
    }

    public void addUser(String uid, UserDB user) {
        dbConnector.writeToDB(uid, user);
    }

    public boolean addEntry(DatabaseValues subPath, String value){
        String path = USER_TABLE.getName() + "/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + subPath.getName()
                + "/" + value;
        return dbConnector.writeToDB(path, value);
    }

    public boolean deleteEntry(DatabaseValues subPath, String value){
        String path = USER_TABLE.getName() + "/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + subPath.getName()
                + "/" + value==null?"":value;
        return dbConnector.deleteFromDB(path, value);
    }

    public boolean listenToEntry(DatabaseValues subPath, String friend){
        ValueEventListener listener;
        String path = USER_TABLE.getName() + "/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + subPath.getName()
                + "/" + friend;
        return dbConnector.writeToDB(path, friend);
    }

}
