package com.joiaapp.joia;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.joiaapp.joia.dto.Group;
import com.joiaapp.joia.dto.Message;
import com.joiaapp.joia.dto.User;
import com.joiaapp.joia.dto.request.CreateGroupRequest;
import com.joiaapp.joia.dto.request.JoinGroupRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arnell on 3/24/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class GroupService {
    private static final String SERVER_BASE_URL = "http://sample-env.qd8vv2zefd.us-west-2.elasticbeanstalk.com";
    private static GroupService instance;
    private RequestQueue requestQueue;
    private MainActivity mainActivity;
    private Group currentGroup;

    private GroupService(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        requestQueue = Volley.newRequestQueue(this.mainActivity.getApplicationContext());
    }

    public static void init(MainActivity mainActivity) {
        if (instance == null) {
            instance = new GroupService(mainActivity);
        }
    }

    public static GroupService getInstance() {
        return instance;
    }

    public void setCurrentGroup(Group currentGroup) {
        DataStorage.getInstance().set("CURRENT_GROUP", currentGroup);
        this.currentGroup = currentGroup;
    }

    public Group getCurrentGroup() {
        if (currentGroup == null) {
            currentGroup = DataStorage.getInstance().get("CURRENT_GROUP", Group.class);
        }
        return currentGroup;
    }

    public void getGroup(String guid, String password, RequestHandler<Group> requestHandler) {
        String url = SERVER_BASE_URL + String.format("/groups/%s.json?password=%s", guid, password);
        GsonCookieRequest request = new GsonCookieRequest<Group>(Request.Method.GET, url, null, requestHandler);
        requestQueue.add(request);
    }

    public void joinGroup(User user, Group group, RequestHandler<Group> requestHandler) {
        JoinGroupRequest joinGroupRequest = new JoinGroupRequest();
        joinGroupRequest.setUserId(user.getId());
        String url = SERVER_BASE_URL + String.format("/groups/%s/join.json", group.getGuid());
        GsonCookieRequest request = new GsonCookieRequest<Group>(Request.Method.POST, url, joinGroupRequest, requestHandler);
        requestQueue.add(request);
    }

    public void createGroup(Group group, RequestHandler<Group> requestHandler) {
        CreateGroupRequest createGroupRequest = new CreateGroupRequest(group);
        String url = SERVER_BASE_URL + "/groups.json";
        GsonCookieRequest request = new GsonCookieRequest<Group>(Request.Method.POST, url, createGroupRequest, requestHandler);
        requestQueue.add(request);
    }

    public void getGroupMessages(Group group, RequestHandler<List<Message>> requestHandler) {
        String url = SERVER_BASE_URL + "/groups/" + group.getGuid() + "/responses.json";
        GsonCookieRequest request = new GsonListCookieRequest<List<Message>>(Request.Method.GET, url, null, requestHandler);
        requestQueue.add(request);
    }

    public void publishGroupMessages(Group group, List<Message> messages, RequestHandler<List<Message>> requestHandler) {
        String url = SERVER_BASE_URL + "/groups/" + group.getGuid() + "/responses.json";
        publishGroupMessage(url, messages, requestHandler, new ArrayList<Message>());
    }

    private void publishGroupMessage(final String url, final List<Message> messages, final RequestHandler<List<Message>> finalRequestHandler, final List<Message> failedPublishes) {
        GsonCookieRequest request = new GsonCookieRequest<Message>(Request.Method.POST, url, messages.get(0), new RequestHandler<Message>() {
            @Override
            public void onResponse(Message response) {
                messages.remove(0);
                if (messages.isEmpty()) {
                    finalRequestHandler.onResponse(failedPublishes);
                } else {
                    publishGroupMessage(url, messages, finalRequestHandler, failedPublishes);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                failedPublishes.add(messages.remove(0));
                if (messages.isEmpty()) {
                    finalRequestHandler.onResponse(failedPublishes);
                } else {
                    publishGroupMessage(url, messages, finalRequestHandler, failedPublishes);
                }
            }
        });
        requestQueue.add(request);
    }
}
