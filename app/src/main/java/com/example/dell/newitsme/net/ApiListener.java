package com.example.dell.newitsme.net;

import org.json.JSONObject;

public interface ApiListener {
    void onResponse(JSONObject response);
    void onErrorResponse(int statusCode, String exceptionMessage);
}
