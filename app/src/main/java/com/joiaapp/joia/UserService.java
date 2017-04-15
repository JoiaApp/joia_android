package com.joiaapp.joia;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.joiaapp.joia.dto.Group;
import com.joiaapp.joia.dto.User;
import com.joiaapp.joia.dto.request.SignInRequest;
import com.joiaapp.joia.requestdto.CreateUser;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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
    private static final String SERVER_BASE_URL = "http://sample-env.qd8vv2zefd.us-west-2.elasticbeanstalk.com";
    private static UserService instance;
    private RequestQueue requestQueue;
    private User currentUser;
    private MainActivity mainActivity;

    private UserService(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        requestQueue = Volley.newRequestQueue(this.mainActivity.getApplicationContext());
    }

    public static void init(MainActivity context) {
        if (instance == null) {
            instance = new UserService(context);

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
    }

    public static UserService getInstance() {
        return instance;
    }

    public void signIn(String email, String password, RequestHandler<User> requestHandler) {
        SignInRequest signInRequest = new SignInRequest(email, password);
        String url = SERVER_BASE_URL + "/users/login.json";
        GsonCookieRequest request = new GsonCookieRequest<User>(Request.Method.POST, url, signInRequest, requestHandler, requestHandler);
        requestQueue.add(request);
    }

    public void setCurrentUser(User currentUser) {
        DataStorage.getInstance().set("CURRENT_USER", currentUser);
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        if (currentUser == null) {
            currentUser = DataStorage.getInstance().get("CURRENT_USER", User.class);
        }
        return currentUser;
    }

    public void logout() {
        currentUser = null;
        CookieManager.getInstance().clearSessionCookie();
        DataStorage.getInstance().remove("CURRENT_USER");
        mainActivity.startSignInProcess();
    }

    public void createUserInGroup(User newUser, final Group group, final RequestHandler<User> requestHandler) {
        createUser(newUser, new RequestHandler<User>() {
            @Override
            public void onResponse(final User response) {
                UserService.getInstance().setCurrentUser(response);
                if (group.getId() == null) {
                    // create group
                    GroupService.getInstance().createGroup(group, new RequestHandler<Group>() {
                        @Override
                        public void onResponse(Group createGroupResponse) {
                            joinGroup(response, createGroupResponse, requestHandler);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            requestHandler.onErrorResponse(error);
                        }
                    });
                } else {
                    joinGroup(response, group, requestHandler);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                requestHandler.onErrorResponse(error);
            }
        });
    }

    private void joinGroup(final User user, Group group, final RequestHandler<User> requestHandler) {
        GroupService.getInstance().joinGroup(user, group, new RequestHandler<Group>(){
            @Override
            public void onResponse(Group response) {
                GroupService.getInstance().setCurrentGroup(response);
                user.getGroups().add(response);
                requestHandler.onResponse(user);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                requestHandler.onErrorResponse(error);
            }
        });
    }

    private void createUser(User newUser, RequestHandler<User> requestHandler) {
        String url = SERVER_BASE_URL + "/users.json";
        CreateUser createUserRequest = new CreateUser(newUser);
        GsonCookieRequest request = new GsonCookieRequest<User>(Request.Method.POST, url, createUserRequest, requestHandler, requestHandler);
        requestQueue.add(request);
    }
}
