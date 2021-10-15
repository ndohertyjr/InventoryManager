package com.zybooks.neildohertyoptionone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserDatabase extends SQLiteOpenHelper {
    //Debug tag
    private static final String TAG = "User Database";
    //Create db version and name
    private static final int VERSION = 13;
    private static final String DATABASE_NAME = "users.db";

    private static UserDatabase mUserDb;

    //Define sort constants
    //FIXME: May be not necessary
    public enum UserSortOrder {ALPHABETIC, DATE_ADDED};

    //Get instance of DB
    public static UserDatabase getInstance(Context context) {
        if (mUserDb == null) {
            mUserDb = new UserDatabase(context);
            Log.d("Good job", "Database created");
        }
        return mUserDb;
    }
    //Create helper
    private UserDatabase (Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    //Create user table columns
    private static final class UserTable {
        private static final String TABLE = "users";
        private static final String COL_ID = "_id";
        private static final String COL_NAMES = "names";
        private static final String COL_PASS = "passwords";
        private static final String COL_ADDED = "added";
        private static final String COL_PHONE = "phone";
        private static final String COL_MIN_QUANTITY = "min_quantity";
    }

    //Create db
    @Override
    public void onCreate(SQLiteDatabase db) {

        //Create subjects table
        db.execSQL("create table " + UserTable.TABLE + " (" +
                UserTable.COL_ID + " integer primary key autoincrement," +
                UserTable.COL_NAMES + " text not null," +
                UserTable.COL_PASS + " text not null," +
                UserTable.COL_ADDED + "," +
                UserTable.COL_PHONE + " text," +
                UserTable.COL_MIN_QUANTITY + " int)");

        Log.d(TAG, "Table created");

    }



    //create db update method
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + UserTable.TABLE);
        onCreate(db);
    }

    //add user profile to db
    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserTable.COL_NAMES, user.getUser());
        values.put(UserTable.COL_PASS, user.getPassword());
        values.put(UserTable.COL_ADDED, user.getDateAdded());
        long id = db.insert(UserTable.TABLE, null, values);
        return id != -1;
    }
    //get user profile
    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sqlQuery = "select * from " + UserTable.TABLE +
                " where " + UserTable.COL_NAMES + " = ? and " +
                UserTable.COL_PASS + " = ?";

        //Create cursor to check rows
        Cursor cursor = db.rawQuery(sqlQuery, new String[] {username, password});

        if (cursor.getCount() <= 0) {
            cursor.close();
            db.close();
            return false;
        }
        else {
            cursor.close();
            db.close();
            return true;
        }
    }

    public String getUser(String username, String password) {
        User user = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String sqlQuery = "select * from " + UserTable.TABLE +
                " where " + UserTable.COL_NAMES + " = ? and " +
                UserTable.COL_PASS + " = ?";

        Cursor cursor = db.rawQuery(sqlQuery, new String[] {username, password});

        if (cursor.moveToFirst()) {
            user = new User();
            user.setUser(cursor.getString(1));
        }

        String userToString = user.getUser();

        return userToString;
    }

    public String getUserPhone(String username) {
        User user = new User();
        SQLiteDatabase db = this.getReadableDatabase();

        String sqlQuery = "select * from " + UserTable.TABLE +
                " where " + UserTable.COL_NAMES + " = ?";

        Cursor cursor = db.rawQuery(sqlQuery, new String[]{username});

        if (cursor.moveToFirst()) {
            user.setmPhoneNumber(cursor.getString(4));
        }

        String phone = user.getmPhoneNumber();

        return phone;
    }

    public int getUserMinQuantity(String username) {
        User user = new User();
        SQLiteDatabase db = this.getReadableDatabase();

        String sqlQuery = "select * from " + UserTable.TABLE +
                " where " + UserTable.COL_NAMES + " = ?";

        Cursor cursor = db.rawQuery(sqlQuery, new String[]{username});

        if (cursor.moveToFirst()) {
            user.setmMinQuantity(cursor.getInt(5));
        }

        int quantity = user.getmMinQuantity();

        return quantity;
    }

    public void updateUserPhone(String user, String phone) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserTable.COL_PHONE, phone);
        db.update(UserTable.TABLE, values,
                UserTable.COL_NAMES + " = ?", new String[] {user});

        Log.d(TAG, "updateUserPhone: Called");
    }

    public void updateMinQuantity(String user, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserTable.COL_MIN_QUANTITY, quantity);
        db.update(UserTable.TABLE, values,
                UserTable.COL_NAMES + " = ?", new String[] {user});

        Log.d(TAG, "updateUserQuantity: Called");
    }


}
