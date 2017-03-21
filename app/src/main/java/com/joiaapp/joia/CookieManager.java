package com.joiaapp.joia;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

/**
 * Created by arnell on 3/16/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class CookieManager {
    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "_srv_session";
    private static CookieManager instance;
    private SharedPreferences preferences;

    private CookieManager() {}

    public static void init(Context context) {
        instance = new CookieManager();
        instance.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static CookieManager getInstance() {
        return instance;
    }

    public void extractSessionCookie(Map<String, String> headers) {
        if (headers.containsKey(SET_COOKIE_KEY)
                && headers.get(SET_COOKIE_KEY).startsWith(SESSION_COOKIE)) {
            String cookie = headers.get(SET_COOKIE_KEY);
            if (cookie.length() > 0) {
                String[] splitCookie = cookie.split(";");
                String[] splitSessionId = splitCookie[0].split("=");
                SharedPreferences.Editor prefEditor = preferences.edit();
                prefEditor.putString(SESSION_COOKIE, splitSessionId[1]);
                prefEditor.apply();
            }
        }
    }

    public void addSessionCookie(Map<String, String> headers) {
        String sessionId = preferences.getString(SESSION_COOKIE, null);
        if (sessionId != null) {
            String builder = SESSION_COOKIE + "=" + sessionId;
            headers.put(COOKIE_KEY, builder);
        }
    }

    public void clearSessionCookie() {
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.remove(SESSION_COOKIE);
        prefEditor.apply();
    }
}
