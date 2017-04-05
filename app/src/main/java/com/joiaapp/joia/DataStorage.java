package com.joiaapp.joia;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by arnell on 3/20/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class DataStorage {
    private static DataStorage instance;
    private SharedPreferences preferences;


    private DataStorage(MainActivity mainActivity) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(mainActivity.getApplicationContext());
    }

    public static void init(MainActivity mainActivity)
    {
        if (instance == null) {
            instance = new DataStorage(mainActivity);
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
        return GsonUtil.gson().fromJson(preference, type);
    }

    public <T> void set(String id, T object) {
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString(id, GsonUtil.gson().toJson(object));
        prefEditor.apply();
    }

    public void remove(String id) {
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.remove(id);
        prefEditor.apply();
    }
}
