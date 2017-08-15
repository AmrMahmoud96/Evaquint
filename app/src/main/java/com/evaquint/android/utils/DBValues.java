package com.evaquint.android.utils;

/**
 * Created by henry on 8/15/2017.
 */

public class DBValues {
    public static final int USER_TABLE = 1;
    public static final int CONTACTS_TABLE = 2;
    public static final int EVENTS_TABLE = 3;

    public static String getString (int code){
        switch (code){
            case USER_TABLE:
                return "Users";
            case CONTACTS_TABLE:
                return "Contacts";
            case EVENTS_TABLE:
                return "Events";
            default:
                return null;
        }
    }
}
