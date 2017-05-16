package com.joiaapp.joia;

import com.android.volley.Response;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by arnell on 4/4/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class GsonListCookieRequest<T> extends GsonCookieRequest<T> {
    private final Response.Listener<T> mListener;

    public GsonListCookieRequest(int method, String url, Object requestBody, ResponseHandler<T> listener) {
        super(method, url, requestBody, listener);
        mListener = listener;
    }

    public GsonListCookieRequest(int method, String url, Object requestBody, Response.Listener<T> listener, Response.ErrorListener errorListener, Response.Listener<T> mListener) {
        super(method, url, requestBody, listener, errorListener);
        this.mListener = mListener;
    }

    @Override
    protected Type getResponseType() {
        Class typeClass = (Class) ((ParameterizedType)((ParameterizedType) mListener.getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getActualTypeArguments()[0];
        return new ListType(typeClass);
    }

    private class ListType implements ParameterizedType {
        private Class clazz;

        public ListType(Class clazz) {
            this.clazz = clazz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[] {clazz};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
