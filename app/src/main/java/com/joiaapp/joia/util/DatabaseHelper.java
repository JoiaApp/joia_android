package com.joiaapp.joia.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.joiaapp.joia.dto.User;

/**
 * Created by arnell on 11/4/2016.
 * Copyright 2017 Joia. All rights reserved.
 */

//TODO: don't do database ops on main thread

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "joiaapp.db";

    private static final String CURRENT_USER_TABLE = "current_user";
    private static final String USER_ID_COL = "user_id";
    private static final String USER_EMAIL_COL = "email";
    private static final String USER_NAME_COL = "name";
    private static final String USER_PASSWORD_COL = "password";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        synchronized (DatabaseHelper.class) {
            if (instance == null) {
                instance = new DatabaseHelper(context);
            }
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CURRENT_USER_TABLE + "(" + USER_EMAIL_COL + " TEXT PRIMARY KEY, " + USER_NAME_COL + " TEXT, " + USER_PASSWORD_COL + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + CURRENT_USER_TABLE);
        onCreate(db);
    }

    public void resetDatabase() {
        onUpgrade(getWritableDatabase(), 0, 0);
    }

    public void setCurrentUser(User user) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(USER_ID_COL, user.getId());
            values.put(USER_EMAIL_COL, user.getEmail());
            values.put(USER_NAME_COL, user.getName());
            values.put(USER_PASSWORD_COL, user.getPassword());
            db.insert(CURRENT_USER_TABLE, null, values);
        }
        finally {
            close(db);
        }
    }

    public User getCurrentUser() {
        Cursor c = null;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            c = db.query(CURRENT_USER_TABLE, new String[]{USER_EMAIL_COL, USER_NAME_COL, USER_PASSWORD_COL}, null, null, null, null, null, null);
            if (c.getCount() == 0) {
                return null;
            }
            c.moveToFirst();
            User current = new User();
            current.setEmail(c.getString(0));
            current.setName(c.getString(1));
            current.setPassword(c.getString(2));
            return current;
        }
        finally {
            close(c, db);
        }
    }

    private void close(SQLiteDatabase db)
    {
//        if (db != null && db.isOpen()) {
//            db.close();
//        }
    }

    private void close(Cursor c, SQLiteDatabase db) {
        if (c != null && !c.isClosed()) {
            c.close();
        }
        //close(db);
    }

    private User generateUser(String name) {
        User u = new User();
        u.setName(name);
        return u;
    }
}
