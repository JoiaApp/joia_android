package com.joiaapp.joia;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

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
    private List<Message> cachedGroupMessages = Collections.emptyList();

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

    public void getUsersGroups(User user, RequestHandler<List<Group>> requestHandler) {
        String url = SERVER_BASE_URL + "/users/" + user.getId() + "/groups.json";
        GsonCookieRequest request = new GsonListCookieRequest<List<Group>>(Request.Method.GET, url, null, requestHandler);
        requestQueue.add(request);
    }

    public void getGroupMembers(Group group, RequestHandler<List<User>> requestHandler) {
        String url = SERVER_BASE_URL + "/groups/" + group.getGuid() + "/members.json";
        GsonCookieRequest request = new GsonListCookieRequest<List<User>>(Request.Method.GET, url, null, requestHandler);
        requestQueue.add(request);
    }

    public Bitmap getGroupMemberImageBitmap(User user) {
        // TODO: save the bitmaps so that they can be reused.
        /* TODO: update usages of this to use a separate thread??
        new Thread(new Runnable() {
            public void run() {
            }
        }).start();
         */
        if (user.getImage() != null) {
            try {
                byte[] imageBytes = Base64.decode(user.getImage(), Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            }
            catch (Exception e) {
                Log.w(TAG, "Failed to decode image", e);
            }
        }
        return BitmapFactory.decodeResource(mainActivity.getApplicationContext().getResources(), R.mipmap.ic_launcher);
    }

    public void getGroupMessages(Group group, final RequestHandler<List<Message>> requestHandler) {
        final GroupService me = this;
        String url = SERVER_BASE_URL + "/groups/" + group.getGuid() + "/responses.json";
        GsonCookieRequest request = new GsonListCookieRequest<List<Message>>(Request.Method.GET, url, null, new RequestHandler<List<Message>>() {
            @Override
            public void onResponse(List<Message> response) {
                me.setCachedGroupMessages(response);
                requestHandler.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                requestHandler.onErrorResponse(error);
            }
        });
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

    private void setCachedGroupMessages(List<Message> cachedGroupMessages) {
        this.cachedGroupMessages = cachedGroupMessages;
    }

    public int getNumberOfNewMessagesTodayFromCache() {
        Date today = new Date();
        int count = 0;
        for (Message message : cachedGroupMessages) {
            if (message.isSameDateAs(today)) {
                count++;
            }
        }
        return count;
    }

    public void logout() {
        DataStorage.getInstance().remove("CURRENT_GROUP");
        cachedGroupMessages = Collections.emptyList();
    }
}
