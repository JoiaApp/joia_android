package com.joiaapp.joia.service;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.joiaapp.joia.GsonCookieRequest;
import com.joiaapp.joia.MainActivity;
import com.joiaapp.joia.ResponseHandler;
import com.joiaapp.joia.dto.Group;
import com.joiaapp.joia.dto.User;
import com.joiaapp.joia.dto.request.CreateUserRequest;
import com.joiaapp.joia.dto.request.SignInRequest;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by arnell on 1/11/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class UserService {
    private String serverBaseUrl;
    private UserService me;
    private RequestQueue requestQueue;
    private User currentUser;
    private MainActivity mainActivity;
    private GroupService groupService;
    private DataStorage dataStorage;
    private CookieManager cookieManager;

    public UserService(MainActivity mainActivity, RequestQueue requestQueue, GroupService groupService, DataStorage dataStorage, CookieManager cookieManager, String serverBaseUrl) {
        me = this;
        this.mainActivity = mainActivity;
        this.requestQueue = requestQueue;
        this.groupService = groupService;
        this.dataStorage = dataStorage;
        this.cookieManager = cookieManager;
        this.serverBaseUrl = serverBaseUrl;
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                        }
                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                        }
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    }
            }, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public void signIn(String email, String password, final ResponseHandler<User> responseHandler) {
        SignInRequest signInRequest = new SignInRequest(email, password);
        String url = serverBaseUrl + "/users/login.json";

        GsonCookieRequest request = new GsonCookieRequest<User>(Request.Method.POST, url, signInRequest, new ResponseHandler<User>() {
            @Override
            public void onResponse(final User userResponse) {
                me.setCurrentUser(userResponse);
                groupService.getUsersGroups(userResponse, new ResponseHandler<List<Group>>() {
                    @Override
                    public void onResponse(List<Group> response) {
                        if (!response.isEmpty()) {
                            groupService.setCurrentGroup(response.get(0));
                            responseHandler.onResponse(userResponse);
                        } else {
                            responseHandler.onErrorResponse(new VolleyError("Unable to determine group."));
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseHandler.onErrorResponse(error);
                    }
                });
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                responseHandler.onErrorResponse(error);
            }
        });
        requestQueue.add(request);
    }

    public void setCurrentUser(User currentUser) {
        dataStorage.set("CURRENT_USER", currentUser);
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        if (currentUser == null) {
            currentUser = dataStorage.get("CURRENT_USER", User.class);
        }
        return currentUser;
    }

    public void logout() {
        currentUser = null;
        cookieManager.clearSessionCookie();
        dataStorage.remove("CURRENT_USER");
        groupService.logout();
        mainActivity.startSignInProcess();
    }

    public void createUserInGroup(User newUser, final Group group, final ResponseHandler<User> responseHandler) {
        createUser(newUser, new ResponseHandler<User>() {
            @Override
            public void onResponse(final User response) {
                me.setCurrentUser(response);
                if (group.getId() == null) {
                    // create group
                    groupService.createGroup(group, new ResponseHandler<Group>() {
                        @Override
                        public void onResponse(Group createGroupResponse) {
                            joinGroup(response, createGroupResponse, responseHandler);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            responseHandler.onErrorResponse(error);
                        }
                    });
                } else {
                    joinGroup(response, group, responseHandler);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                responseHandler.onErrorResponse(error);
            }
        });
    }

    private void joinGroup(final User user, Group group, final ResponseHandler<User> responseHandler) {
        groupService.joinGroup(user, group, new ResponseHandler<Group>(){
            @Override
            public void onResponse(Group response) {
                groupService.setCurrentGroup(response);
                user.getGroups().add(response);
                responseHandler.onResponse(user);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                responseHandler.onErrorResponse(error);
            }
        });
    }

    private void createUser(User newUser, ResponseHandler<User> responseHandler) {
        String url = serverBaseUrl + "/users.json";
        CreateUserRequest createUserRequest = new CreateUserRequest(newUser);
        GsonCookieRequest request = new GsonCookieRequest<User>(Request.Method.POST, url, createUserRequest, responseHandler, responseHandler);
        requestQueue.add(request);
    }
}
