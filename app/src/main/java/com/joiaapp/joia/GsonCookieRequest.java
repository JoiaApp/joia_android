package com.joiaapp.joia;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arnell on 3/20/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class GsonCookieRequest<T> extends Request<T> {
    private static Gson gson = new Gson();
    /** Default charset for JSON request. */
    protected static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    private final Listener<T> mListener;
    private final Object mRequestBody;

    public GsonCookieRequest(int method, String url, Object requestBody, Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
        mRequestBody = requestBody;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        CookieManager.getInstance().extractSessionCookie(response.headers);
        Class typeClass = ((Class) ((ParameterizedType) mListener.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return Response.success((T)gson.fromJson(jsonString, typeClass), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : gson.toJson(mRequestBody).getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<>();
        }

        CookieManager.getInstance().addSessionCookie(headers);

        return headers;
    }
}