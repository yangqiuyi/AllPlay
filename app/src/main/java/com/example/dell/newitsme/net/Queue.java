package com.example.dell.newitsme.net;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Queue {
    private static Queue _queue;

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
