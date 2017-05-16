package com.joiaapp.joia;

import com.android.volley.VolleyError;

/**
 * Created by arnell on 5/11/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public abstract class MiddleManResponseHandler<T> extends ResponseHandler<T> {
    private ResponseHandler<T> actualResponseHandler;

    public MiddleManResponseHandler(ResponseHandler<T> responseHandler) {
        actualResponseHandler = responseHandler;
    }

    @Override
    public void onResponse(T response) {
        middleManHandler(response);
        actualResponseHandler.onResponse(response);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        actualResponseHandler.onErrorResponse(error);
    }

    public abstract void middleManHandler(T response);
}
