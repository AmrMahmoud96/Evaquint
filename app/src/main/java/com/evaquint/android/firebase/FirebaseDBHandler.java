package com.evaquint.android.firebase;

import com.evaquint.android.firebase.dataStructures.UserDB;
import com.evaquint.android.utils.database.DBConnector;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by henry on 8/22/2017.
 */

public final class FirebaseDBHandler {
    public static void updateUser(FirebaseUser fUser) {
        DBConnector test =new DBConnector("user");
        test.writeToDB(fUser.getUid(), new UserDB(fUser.getProviders(),
                fUser.getDisplayName(), fUser.getEmail(), "6476731234"));
    }
}
