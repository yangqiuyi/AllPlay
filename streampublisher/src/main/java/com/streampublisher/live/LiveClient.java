package com.streampublisher.live;


import android.util.Log;
import java.io.IOException;

public class LiveClient {
    public static final String TAG = "LiveClient";
    public interface ResultListener {
        void onLiveQuerySuccess(String url, String token);
        void onLiveQueryFail(String reason);
    }

    public static final String PROTOCOL = "rtmp";//soup,dls
    public LiveRequest play(String protocol,String url, String stream, int region, String client,String session, ResultListener listener) throws IOException {

       // url += "?name=PlayStream&protocol="+protocol+"&streamname=" + stream + "&region=" + region + "&player=" + client + "&quality=0";
        url += "?name=PlayStream&protocol="+protocol+"&streamname=" + stream  + "&player=" + client + "&quality=0";
        Log.d(TAG, "play," + url);
        return new HttpLiveRequest(url, listener);
    }

    public LiveRequest publish(String url, String stream, int region, String client, String session, ResultListener listener) throws IOException {
        url += "?name=CreateStream&protocol="+PROTOCOL+"region=" +region+"&streamname=" + stream + "&publisher=" + client + "&sessionid=" + session + "&quality=0";
        //Log.d(TAG,"publish:%s", url);
        return new HttpLiveRequest(url, listener);
    }

    public LiveRequest publish(String url,  ResultListener listener) throws IOException {
        //Log.d(TAG,"publish:%s", url);
        return new HttpLiveRequest(url, listener);
    }
}
