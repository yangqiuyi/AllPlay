package com.example.net;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Queue {
    private static Queue _queue;//单例模式

    public   RequestQueue _requestQueue = null;

    public static Queue inst(){
        if (_queue == null){
            _queue = new Queue();
        }
        return _queue;
    }

    public  void init(Context context){
        _requestQueue = Volley.newRequestQueue(context);
    }
}
