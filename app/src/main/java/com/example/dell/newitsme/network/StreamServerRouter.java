package com.example.dell.newitsme.network;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.dell.newitsme.ItsmeApplication;


public class StreamServerRouter {

    public static final String TAG = "StreamServerRouter";

    private static StreamServerRouter g_inst;
    private int _retryCount;
    private RequestQueue _requestQueue = null;
    private int _forcusRegion = -1;

    public static StreamServerRouter inst(){
        if (g_inst == null){
            g_inst = new StreamServerRouter();
            g_inst._requestQueue = Volley.newRequestQueue(ItsmeApplication.getInstance().getApplicationContext());
        }

        return g_inst;
    }

    //IP ---> 地区
    public int hostToRegion(String host) {

        if (_forcusRegion >= 0){
            Log.d(TAG,"hostToRegion,foucs region:" + _forcusRegion);
            return _forcusRegion;
        }

        return -1;

    }


}
