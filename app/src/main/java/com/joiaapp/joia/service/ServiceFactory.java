package com.joiaapp.joia.service;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by arnell on 5/24/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class ServiceFactory {
    private static final String SERVER_BASE_URL = "http://sample-env.qd8vv2zefd.us-west-2.elasticbeanstalk.com";
    private static ServiceFactory instance;

    private CookieManager cookieManager;
    private DataStorage dataStorage;
    private GroupService groupService;
    private PromptService promptService;
    private UserService userService;

    private ServiceFactory(Context applicationContext) {
        RequestQueue requestQueue = Volley.newRequestQueue(applicationContext);
        cookieManager = new CookieManager(applicationContext);
        dataStorage = new DataStorage(applicationContext);
        groupService = new GroupService(applicationContext, dataStorage, requestQueue, SERVER_BASE_URL);
        promptService = new PromptService(requestQueue, SERVER_BASE_URL);
        userService = new UserService(requestQueue, groupService, dataStorage, cookieManager, SERVER_BASE_URL);
    }

    public static void init(Context applicationContext) {
        instance = new ServiceFactory(applicationContext);
    }

    public static CookieManager getCookieManager() {
        return instance.cookieManager;
    }

    public static DataStorage getDataStorage() {
        return instance.dataStorage;
    }

    public static GroupService getGroupService() {
        return instance.groupService;
    }

    public static PromptService getPromptService() {
        return instance.promptService;
    }

    public static UserService getUserService() {
        return instance.userService;
    }
}
