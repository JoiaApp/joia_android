package com.joiaapp.joia.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.joiaapp.joia.util.GsonUtil;

/**
 * Created by arnell on 3/20/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class DataStorage {
    private SharedPreferences preferences;


    public DataStorage(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
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
