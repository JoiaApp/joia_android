package com.joiaapp.joia;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by arnell on 1/15/2017.
 */

public abstract class RequestHandler implements Response.Listener<JSONObject>, Response.ErrorListener {

    @Override
    public abstract void onResponse(JSONObject response);

    @Override
    public abstract void onErrorResponse(VolleyError error);
}
