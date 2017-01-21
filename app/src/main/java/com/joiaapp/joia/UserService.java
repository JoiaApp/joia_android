package com.joiaapp.joia;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

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
 */

public class UserService {
    private static final String SERVER_BASE_URL = "https://joia-staging.us-west-2.elasticbeanstalk.com";
    private static UserService instance;
    private RequestQueue requestQueue;

    private UserService(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized UserService getInstance(Context context) {
        synchronized (UserService.class) {
            if (instance == null) {
                instance = new UserService(context);

                SSLContext ctx = null;
                try {
                    ctx = SSLContext.getInstance("TLS");
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
                } catch (NoSuchAlgorithmException | KeyManagementException e) {
                    e.printStackTrace();
                }
                HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());

                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }
        }
        return instance;
    }


    public void signIn(String email, String password, RequestHandler requestHandler) {
        JSONObject signInRequest = new JSONObject();
        try {
            signInRequest.put("email", email);
            signInRequest.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = SERVER_BASE_URL + "/users/login.json";

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, signInRequest,
                requestHandler,
                requestHandler
        );
        // Add the request to the RequestQueue.
        requestQueue.add(jsonObjectRequest);
    }
}
