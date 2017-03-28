package com.joiaapp.joia;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
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
    private static final String SERVER_BASE_URL = "http://ec2-35-167-58-219.us-west-2.compute.amazonaws.com";
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

    public void createUser(User newUser, RequestHandler requestHandler) {
        String url = SERVER_BASE_URL + "/users.json";
        CreateUser createUserRequest = new CreateUser(newUser);
        GsonCookieRequest request = new GsonCookieRequest<User>(Request.Method.POST, url, createUserRequest, requestHandler, requestHandler);
        requestQueue.add(request);
    }

    public void logout() {
        currentUser = null;
        CookieManager.getInstance().clearSessionCookie();
        DataStorage.getInstance().remove("CURRENT_USER");
        mainActivity.startSignInProcess();
    }
}
