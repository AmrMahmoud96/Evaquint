package com.evaquint.android.values;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by henry on 8/6/2017.
 */

public class DBManager extends SQLiteOpenHelper {
        public static final String DATABASE_NAME = "Evaquint.db";
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
        this.db.execSQL(
                "create table contacts " +
                        "(id integer primary key, name text,phone text,email text, street text,place text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public void createTable(String tableName) {
        String sqlReq = "CREATE TABLE IF NOT EXISTS "+ tableName+" ";
        switch(tableName){
            case "User": {
                sqlReq +="(" +
                        "user_id INTEGER PRIMARY KEY, " +
                        "given_name VARCHAR" +
                        "family_name VARCHAR" +
                        "email VARCHAR" +
                        "phone INTEGER" +
                        ");";
                break;
            }
            case "Contacts": {
                sqlReq +="(" +
                        "user_id INTEGER PRIMARY KEY, " +
                        "given_name VARCHAR" +
                        "family_name VARCHAR" +
                        "email VARCHAR" +
                        "phone INTEGER" +
                        ");";
                break;
            }
            default:
                return;
        }
        db.execSQL(sqlReq);
    }
    public boolean insertUser(String tableName, GoogleSignInAccount acct) {
        String sqlReq = "INSERT INTO "+ tableName+" ";
        switch(tableName){
            case "User": {
                sqlReq +=" (" +
                        "user_id INTEGER PRIMARY KEY, " +
                        "given_name VARCHAR" +
                        "family_name VARCHAR" +
                        "email VARCHAR" +
                        "phone INTEGER" +
                        ")";
                sqlReq +=" VALUES (" +
                        "++, " +
                        "given_name VARCHAR" +
                        "family_name VARCHAR" +
                        "email VARCHAR" +
                        "phone INTEGER" +
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

    public void retrieveData(SQLiteDatabase db, String tableName) {
        Cursor c = db.rawQuery("SELECT * FROM " + tableName, null);

        int Column1 = c.getColumnIndex("Field1");
        int Column2 = c.getColumnIndex("Field2");

        // Check if our result was valid.
        c.moveToFirst();
        String data = "";
        if (c != null) {
            // Loop through all Results
            do {
                String Name = c.getString(Column1);
                int Age = c.getInt(Column2);
                data = data + Name + "/" + Age + "\n";
            } while (c.moveToNext());
        }
    }


}