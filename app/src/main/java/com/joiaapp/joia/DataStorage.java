package com.joiaapp.joia;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

/**
 * Created by arnell on 3/20/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class DataStorage {
    private static DataStorage instance;
    private SharedPreferences preferences;
    private Gson gson;


    private DataStorage(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        gson = new Gson();
    }

    public static void init(Context context)
    {
        if (instance == null) {
            instance = new DataStorage(context);
        }
    }

    public static DataStorage getInstance()
    {
        return instance;
    }

    public <T> T get(String id, Class<T> type) {
        String preference = preferences.getString(id, null);
        if (preference == null) {
            return null;
        }
        return gson.fromJson(preference, type);
    }

    public <T> void set(String id, T object) {
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString(id, gson.toJson(object));
        prefEditor.apply();
    }

    public void remove(String id) {
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.remove(id);
        prefEditor.apply();
    }
}
