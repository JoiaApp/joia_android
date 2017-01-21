package com.joiaapp.joia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by arnell on 11/4/2016.
 */

//TODO: don't do database ops on main thread

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "joiaapp.db";

    private static final String CURRENT_USER_TABLE = "current_user";


    private static final String USER_TABLE = "user";
    private static final String USER_ID_COL = "user_id";
    private static final String USER_EMAIL_COL = "email";
    private static final String USER_NAME_COL = "name";
    private static final String USER_PASSWORD_COL = "password";
    private static final String USER_NOTIF_SCHED_COL = "notification_schedule";

    private static final String MESSAGE_TABLE = "message";
    private static final String MESSAGE_ID_COL = "message_id";
    private static final String MESSAGE_TEXT_COL = "text";
    private static final String MESSAGE_USER_COL = "user_id";
    private static final String MESSAGE_DATE_COL = "date";

    private static final String MESSAGE_MENTION_TABLE = "message_mention";
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
        db.execSQL("CREATE TABLE " + CURRENT_USER_TABLE + "(" + USER_EMAIL_COL + " TEXT PRIMARY KEY, " + USER_NAME_COL + " TEXT, " + USER_PASSWORD_COL + " TEXT)");;
        db.execSQL("CREATE TABLE " + USER_TABLE + "(" + USER_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " + USER_EMAIL_COL + " TEXT, " + USER_NAME_COL + " TEXT)");
        db.execSQL("CREATE TABLE " + MESSAGE_TABLE + "(" + MESSAGE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " + MESSAGE_TEXT_COL + " TEXT, " + MESSAGE_USER_COL + " INTEGER, " + MESSAGE_DATE_COL + " INTEGER)");
        db.execSQL("CREATE TABLE " + MESSAGE_MENTION_TABLE + "(" + MESSAGE_ID_COL + " INTEGER, " + USER_ID_COL + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + CURRENT_USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_MENTION_TABLE);
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

    public void createUser(User user) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(USER_ID_COL, user.getId());
            values.put(USER_EMAIL_COL, user.getEmail());
            values.put(USER_NAME_COL, user.getName());
            db.insert(USER_TABLE, null, values);
        }
        finally {
            close(db);
        }
    }

    public void createMessage(Message message) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MESSAGE_TEXT_COL, message.getFullText());
            values.put(MESSAGE_USER_COL, message.getUserId());
            values.put(MESSAGE_DATE_COL, message.getDate().getTime());
            long rowId = db.insert(MESSAGE_TABLE, null, values);

            for (User mention : message.getMentions()) {
                ContentValues messageMentionValues = new ContentValues();
                messageMentionValues.put(MESSAGE_ID_COL, rowId);
                messageMentionValues.put(USER_ID_COL, mention.getId());
                db.insert(MESSAGE_MENTION_TABLE, null, messageMentionValues);
            }
        }
        finally {
            close(db);
        }
    }

    public List<Message> getMessages() {
        Cursor c = null;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            c = db.query(MESSAGE_TABLE, new String[]{MESSAGE_ID_COL, MESSAGE_TEXT_COL, MESSAGE_USER_COL, MESSAGE_DATE_COL}, null, null, null, null, MESSAGE_DATE_COL + " DESC", null);
            List<Message> messages = new ArrayList<>(c.getCount());
            while (c.moveToNext()) {
                Message m = new Message();
                m.setId(c.getInt(0));
                m.setText(c.getString(1));
                m.setUserId(c.getInt(2));
                m.setDate(new Date(c.getLong(3)));
                messages.add(m);
            }
            return messages;
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

    public List<User> getGroupMembers() {
        List<User> members = new ArrayList<>();
        members.add(generateUser("Jane Silva"));
        members.add(generateUser("Daisy Watkins"));
        members.add(generateUser("Jessie Wallace"));
        members.add(generateUser("Michael Simpson"));
        members.add(generateUser("Edith Turner"));
        members.add(generateUser("Jayden Barnett"));
        members.add(generateUser("Shelly Kennedy"));
        members.add(generateUser("Jessica King"));
        return members;
    }
    private User generateUser(String name) {
        User u = new User();
        u.setName(name);
        return u;
    }
}
