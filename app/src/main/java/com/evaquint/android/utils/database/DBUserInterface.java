package com.evaquint.android.utils.database;

import com.evaquint.android.utils.dataStructures.UserDB;
import com.evaquint.android.utils.database.DBConnector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by henry on 8/22/2017.
 */

public final class DBUserInterface {
    public static void addUser(FirebaseUser fUser) {
        DBConnector test = new DBConnector("user");
        test.writeToDB(fUser.getUid(), new UserDB(fUser.getProviders(),
                fUser.getDisplayName(), fUser.getEmail(), "6476731234"));
    }

    public static void addUser(String uid, UserDB user) {
        DBConnector test =new DBConnector("user");
        test.writeToDB(uid, user);
    }

    public static boolean addFriend(String friend, int f){
        String path = "user/" + FirebaseAuth.getInstance().getCurrentUser().getUid()+"/friend/"+friend;
        return true;
    }

    private static boolean addProvider(String provider){
        String path = "user/" + FirebaseAuth.getInstance().getCurrentUser().getUid()+"/provider/"+provider;
        return true;
    }

}
