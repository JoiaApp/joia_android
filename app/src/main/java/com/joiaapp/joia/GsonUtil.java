package com.joiaapp.joia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by arnell on 4/4/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class GsonUtil {
    private static Gson gson;
    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        gson = gsonBuilder.create();
    }

    public static Gson gson() {
        return gson;
    }
}
