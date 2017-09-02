package com.joiaapp.joia.service;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.joiaapp.joia.util.GsonCookieRequest;
import com.joiaapp.joia.util.GsonListCookieRequest;
import com.joiaapp.joia.util.ResponseHandler;
import com.joiaapp.joia.dto.Prompt;

import java.util.List;

/**
 * Created by arnell on 4/15/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class PromptService {
    private String serverBaseUrl;
    private RequestQueue requestQueue;

    public PromptService(RequestQueue requestQueue, String serverBaseUrl) {
        this.requestQueue = requestQueue;
        this.serverBaseUrl = serverBaseUrl;
    }

    public void getPrompts(ResponseHandler<List<Prompt>> responseHandler) {
        String url = serverBaseUrl + "/prompts.json";
        GsonCookieRequest request = new GsonListCookieRequest<List<Prompt>>(Request.Method.GET, url, null, responseHandler);
        requestQueue.add(request);
    }
}
