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
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        this.db=db;
        createTable("user_test");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS user-test");
        onCreate(db);
    }

    public void createTable(String tableName) {
        String sqlReq = "CREATE TABLE IF NOT EXISTS "+ tableName+" ";
        switch(tableName){
            case "User": {
                sqlReq +="(" +
                        "user_id INTEGER PRIMARY KEY, " +
                        "given_name VARCHAR，" +
                        "family_name VARCHAR，" +
                        "email VARCHAR UNIQUE，" +
                        "phone INTEGER UNIQUE" +
                        ");";
                break;
            }
            case "Contacts": {
                sqlReq +="(" +
                        "user_id INTEGER PRIMARY KEY, " +
                        "given_name VARCHAR，" +
                        "family_name VARCHAR，" +
                        "email VARCHAR UNIQUE，" +
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
        String sqlReq = "INSERT INTO "+ tableName+" ";
        switch(tableName){
            case "User": {
                sqlReq +=" (" +
                        "user_id VARCHAR PRIMARY KEY, " +
                        "given_name VARCHAR，" +
                        "family_name VARCHAR，" +
                        "email VARCHAR UNIQUE，" +
                        "phone VARCHAR UNIQUE" +
                        ")";
                sqlReq +=" VALUES (" +
                        "41244124" + ", " +
                        acct.getGivenName() + ", " +
                        acct.getFamilyName() + ", " +
                        acct.getEmail() + ", " +
                        "787608" +
                        ");";
                break;
            }
            case "Contacts": {
                sqlReq +=" (" +
                        "user_id INTEGER PRIMARY KEY, " +
                        "given_name VARCHAR" +
                        "family_name VARCHAR" +
                        "email VARCHAR" +
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

        int Column1 = c.getColumnIndex("user_id");
        int Column2 = c.getColumnIndex("given_name");
        int Column3 = c.getColumnIndex("family_name");
        int Column4 = c.getColumnIndex("email");
        int Column5 = c.getColumnIndex("phone");

        // Check if our result was valid.
        c.moveToFirst();
        String [] data=null;
        int i = 0;
        if (c != null) {
            // Loop through all Results
            do {
                data[i++] = c.getString(Column1);
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