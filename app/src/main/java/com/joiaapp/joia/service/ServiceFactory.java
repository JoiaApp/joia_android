package com.joiaapp.joia.service;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.joiaapp.joia.MainActivity;

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

    private ServiceFactory(MainActivity mainActivity) {
        RequestQueue requestQueue = Volley.newRequestQueue(mainActivity.getApplicationContext());
        cookieManager = new CookieManager(mainActivity);
        dataStorage = new DataStorage(mainActivity);
        groupService = new GroupService(mainActivity, dataStorage, requestQueue, SERVER_BASE_URL);
        promptService = new PromptService(requestQueue, SERVER_BASE_URL);
        userService = new UserService(mainActivity, requestQueue, groupService, dataStorage, cookieManager, SERVER_BASE_URL);
    }

    public static void init(MainActivity mainActivity) {
        if (instance == null) {
            instance = new ServiceFactory(mainActivity);
        }
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
