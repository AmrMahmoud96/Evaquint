package com.evaquint.android.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.IOException;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by henry on 8/6/2017.
 */

public class DBManager extends SQLiteOpenHelper {
        public static final String DATABASE_NAME = "test";
        public static final String CONTACTS_TABLE_NAME = "contacts";
        public static final String CONTACTS_COLUMN_ID = "id";
        public static final String CONTACTS_COLUMN_NAME = "name";
        public static final String CONTACTS_COLUMN_EMAIL = "email";
        public static final String CONTACTS_COLUMN_STREET = "street";
        public static final String CONTACTS_COLUMN_CITY = "place";
        public static final String CONTACTS_COLUMN_PHONE = "phone";
        private HashMap hp;
        SQLiteDatabase db=null;

    public DBManager(Context context) {
        super(context, DATABASE_NAME , null, 1);
        this.db=getWritableDatabase();
        createTable("User");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        this.db=db;
        createTable("User");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS user-test");
        onCreate(db);
    }

    public void createTable(String tableName) {
        db.execSQL("DROP TABLE IF EXISTS User");
        String sqlReq = "CREATE TABLE IF NOT EXISTS "+ tableName+" ";
        switch(tableName){
            case "User": {
                sqlReq +="(" +
                        "user_id TEXT PRIMARY KEY, " +
                        "given_name TEXT, " +
                        "family_name TEXT, " +
                        "email TEXT UNIQUE, " +
                        "phone TEXT UNIQUE" +
                        ");";
                break;
            }
            case "Contacts": {
                sqlReq +="(" +
                        "user_id INTEGER PRIMARY KEY, " +
                        "given_name TEXT, " +
                        "family_name TEXT, " +
                        "email TEXT UNIQUE, " +
                        "phone INTEGER UNIQUE" +
                        ");";
                break;
            }
            default:
                return;
        }
//        this.getWritableDatabase().execSQL(sqlReq);
        db.execSQL(sqlReq);
    }
    public boolean insertUser(String tableName, GoogleSignInAccount acct) {
        String sqlReq = "INSERT OR REPLACE INTO "+ tableName+" ";
        switch(tableName){
            case "User": {
                sqlReq +="(" +
                        "user_id, " +
                        "given_name, " +
                        "family_name, " +
                        "email, " +
                        "phone" +
                        ")";
                sqlReq +=" VALUES (\'" +
                        "41244124" + "\', \'" +
                        acct.getGivenName() + "\', \'" +
                        acct.getFamilyName() + "\', \'" +
                        acct.getEmail() + "\', \'" +
                        "787608\'" +
                        ");";
                break;
            }
            case "Contacts": {
                sqlReq +=" (" +
                        "user_id INTEGER PRIMARY KEY, " +
                        "given_name TEXT" +
                        "family_name TEXT" +
                        "email TEXT" +
                        "phone INTEGER" +
                        ");";
                break;
            }
            default:
                return false;
        }
        db.execSQL(sqlReq);
        return true;
    }

    public String [] retrieveData(String tableName) {
        Cursor c = db.rawQuery("SELECT * FROM " + tableName, null);
        String [] columnHeaders= {"user_id","given_name","family_name","email","phone"};
        int [] columns = new int[columnHeaders.length];
        int i =0;
        for (String col : columnHeaders){
            columns[i++]= c.getColumnIndex(col);
        }

        // Check if our result was valid.
        c.moveToFirst();
        String [] data=new String[10];

        if (c != null) {
            // Loop through all Results
            do {
                i = 0;
                for (int col : columns) {
                    data[i++] = c.getString(col);
                }

            } while (c.moveToNext());
        }
        return data;
    }

    private int getCount(String name) {
        Cursor c = null;
        try {
            String query = "select count(*) from TableName where name = ?";
            c = db.rawQuery(query, new String[] {name});
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
            return 0;
        }
        finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }


}