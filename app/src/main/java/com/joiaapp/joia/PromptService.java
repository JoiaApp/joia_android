package com.joiaapp.joia;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.joiaapp.joia.dto.Prompt;

import java.util.List;

/**
 * Created by arnell on 4/15/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class PromptService {
    private static final String SERVER_BASE_URL = "http://sample-env.qd8vv2zefd.us-west-2.elasticbeanstalk.com";
    private static PromptService instance;
    private RequestQueue requestQueue;
    private MainActivity mainActivity;

    private PromptService(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        requestQueue = Volley.newRequestQueue(this.mainActivity.getApplicationContext());
    }

    public static void init(MainActivity context) {
        if (instance == null) {
            instance = new PromptService(context);
        }
    }

    public static PromptService getInstance() {
        return instance;
    }

    public void getPrompts(RequestHandler<List<Prompt>> requestHandler) {
        String url = SERVER_BASE_URL + "/prompts.json";
        GsonCookieRequest request = new GsonListCookieRequest<List<Prompt>>(Request.Method.GET, url, null, requestHandler);
        requestQueue.add(request);
    }
}
