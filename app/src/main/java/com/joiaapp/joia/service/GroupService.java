package com.joiaapp.joia.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.joiaapp.joia.R;
import com.joiaapp.joia.dto.Group;
import com.joiaapp.joia.dto.Mention;
import com.joiaapp.joia.dto.Message;
import com.joiaapp.joia.dto.User;
import com.joiaapp.joia.dto.request.CreateGroupRequest;
import com.joiaapp.joia.dto.request.InviteRequest;
import com.joiaapp.joia.dto.request.JoinGroupRequest;
import com.joiaapp.joia.util.GsonCookieRequest;
import com.joiaapp.joia.util.GsonListCookieRequest;
import com.joiaapp.joia.util.MiddleManResponseHandler;
import com.joiaapp.joia.util.ResponseHandler;

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
    private String serverBaseUrl;
    private RequestQueue requestQueue;
    private Context context;
    private DataStorage dataStorage;
    private Group currentGroup;
    private List<Message> cachedGroupMessages = Collections.emptyList();
    private List<User> cachedGroupMembers = Collections.emptyList();

    public GroupService(Context context, DataStorage dataStorage, RequestQueue requestQueue, String serverBaseUrl) {
        this.context = context;
        this.dataStorage = dataStorage;
        this.requestQueue = requestQueue;
        this.serverBaseUrl = serverBaseUrl;
    }

    public void setCurrentGroup(Group currentGroup) {
        dataStorage.set("CURRENT_GROUP", currentGroup);
        this.currentGroup = currentGroup;
    }

    public Group getCurrentGroup() {
        if (currentGroup == null) {
            currentGroup = dataStorage.get("CURRENT_GROUP", Group.class);
        }
        return currentGroup;
    }

    public void getGroup(String guid, String password, ResponseHandler<Group> responseHandler) {
        String url = serverBaseUrl + String.format("/groups/%s.json?password=%s", guid, password);
        GsonCookieRequest request = new GsonCookieRequest<Group>(Request.Method.GET, url, null, responseHandler);
        requestQueue.add(request);
    }

    public void joinGroup(User user, Group group, ResponseHandler<Group> responseHandler) {
        JoinGroupRequest joinGroupRequest = new JoinGroupRequest();
        joinGroupRequest.setUserId(user.getId());
        String url = serverBaseUrl + String.format("/groups/%s/join.json", group.getGuid());
        GsonCookieRequest request = new GsonCookieRequest<Group>(Request.Method.POST, url, joinGroupRequest, responseHandler);
        requestQueue.add(request);
    }

    public void createGroup(Group group, ResponseHandler<Group> responseHandler) {
        CreateGroupRequest createGroupRequest = new CreateGroupRequest(group);
        String url = serverBaseUrl + "/groups.json";
        GsonCookieRequest request = new GsonCookieRequest<Group>(Request.Method.POST, url, createGroupRequest, responseHandler);
        requestQueue.add(request);
    }

    public void getUsersGroups(User user, ResponseHandler<List<Group>> responseHandler) {
        String url = serverBaseUrl + "/users/" + user.getId() + "/groups.json";
        GsonCookieRequest request = new GsonListCookieRequest<List<Group>>(Request.Method.GET, url, null, responseHandler);
        requestQueue.add(request);
    }

    public void getGroupMembers(final Group group, ResponseHandler<List<User>> responseHandler) {
        String url = serverBaseUrl + "/groups/" + group.getGuid() + "/members.json";
        GsonCookieRequest request = new GsonListCookieRequest<List<User>>(Request.Method.GET, url, null, new MiddleManResponseHandler<List<User>>(responseHandler) {
            @Override
            public void middleManHandler(List<User> response) {
                group.setMembers(response);
            }
        });
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
        if (user.getId() == null) {
            return BitmapFactory.decodeResource(context.getResources(), R.mipmap.plus);
        }
        if (user.getImage() != null) {
            try {
                byte[] imageBytes = Base64.decode(user.getImage(), Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            } catch (Exception e) {
                Log.w(TAG, "Failed to decode image", e);
            }
        }
        return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
    }

    public void getGroupMessages(Group group, final ResponseHandler<List<Message>> responseHandler) {
        final GroupService me = this;
        String url = serverBaseUrl + "/groups/" + group.getGuid() + "/responses.json";
        GsonCookieRequest request = new GsonListCookieRequest<List<Message>>(Request.Method.GET, url, null, new MiddleManResponseHandler<List<Message>>(responseHandler) {
            @Override
            public void middleManHandler(List<Message> response) {
                me.setCachedGroupMessages(response);
            }
        });
        requestQueue.add(request);
    }

    public void publishGroupMessages(Group group, List<Message> messages, ResponseHandler<List<Message>> responseHandler) {
        String url = serverBaseUrl + "/groups/" + group.getGuid() + "/responses.json";
        publishGroupMessage(url, messages, responseHandler, new ArrayList<Message>());
    }

    private void publishGroupMessage(final String url, final List<Message> messages, final ResponseHandler<List<Message>> finalResponseHandler, final List<Message> failedPublishes) {
        final Message currentMessage = messages.get(0);
        GsonCookieRequest request = new GsonCookieRequest<Message>(Request.Method.POST, url, currentMessage, new ResponseHandler<Message>() {
            @Override
            public void onResponse(Message response) {
                currentMessage.setId(response.getId());
                publishMentions(currentMessage, getCurrentGroup());
                messages.remove(currentMessage);
                if (messages.isEmpty()) {
                    finalResponseHandler.onResponse(failedPublishes);
                } else {
                    publishGroupMessage(url, messages, finalResponseHandler, failedPublishes);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                failedPublishes.add(messages.remove(0));
                if (messages.isEmpty()) {
                    finalResponseHandler.onResponse(failedPublishes);
                } else {
                    publishGroupMessage(url, messages, finalResponseHandler, failedPublishes);
                }
            }
        });
        requestQueue.add(request);
    }

    private void publishMentions(Message message, Group group) {
        String url = serverBaseUrl + "/groups/" + group.getGuid() + "/mentions.json";
        for (final Mention currentMention : message.getMentions()) {
            currentMention.setResponse_id(message.getId());
            GsonCookieRequest request = new GsonCookieRequest<Mention>(Request.Method.POST, url, currentMention, new ResponseHandler<Mention>() {
                @Override
                public void onResponse(Mention response) {
                    currentMention.setId(response.getId());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    //TODO: handle failed saves, or better yet, update the server to accept a full message with mentions on it
                    Toast toast = Toast.makeText(context, "Failed to add mentions to a message.", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
            requestQueue.add(request);
        }
    }

    public void sendInvite(String email, Group group, ResponseHandler<String> responseHandler) {
        String url = serverBaseUrl + "/groups/" + group.getGuid() + "/invite.json";
        InviteRequest inviteRequest = new InviteRequest(email);
        GsonCookieRequest request = new GsonCookieRequest<String>(Request.Method.POST, url, inviteRequest, responseHandler);
        requestQueue.add(request);
    }

    private void setCachedGroupMessages(List<Message> cachedGroupMessages) {
        this.cachedGroupMessages = cachedGroupMessages;
    }

    private void setCachedGroupMembers(List<User> cachedGroupMembers) {
        this.cachedGroupMembers = cachedGroupMembers;
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
        currentGroup = null;
        dataStorage.remove("CURRENT_GROUP");
        cachedGroupMessages = Collections.emptyList();
    }
}
