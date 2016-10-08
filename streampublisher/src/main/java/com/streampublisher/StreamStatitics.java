package com.streampublisher;

import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;




public class StreamStatitics {
    public static final String TAG = "StreamStatitics";
    int retryQueryCount; //重连次数
    int interupCount;//中断次数
    long liveQuerySlaps;//查询视频流地址所用的时间
    long streamConnectSlaps;//从连接视频流到第一帧所用时间
    long tickBegin;

    public String queryUrl = "";
    public String streamUrl = "";

    private static StreamStatitics g_shareInstance;
    public static StreamStatitics shareInstance(){
        if (g_shareInstance == null){
            g_shareInstance = new StreamStatitics();
        }
        return g_shareInstance;
    }

    public void beginLiveQuery(String url) {
        Log.i(TAG, "beginLiveQuery," + url);
        this.queryUrl = url;
        tickBegin = System.currentTimeMillis();
        liveQuerySlaps = -1;
        streamUrl = "";
    }
    public long endLiveQuery(){
        liveQuerySlaps = System.currentTimeMillis() - tickBegin;
        return liveQuerySlaps;
    }

    public void beginStreamConnect(String streamUrl) {
        Log.i(TAG,"beginStreamConnect,"+streamUrl);
        tickBegin = System.currentTimeMillis();
        streamConnectSlaps = -1;
        this.streamUrl = streamUrl;
    }

    public void endStreamConnect(){
        streamConnectSlaps = System.currentTimeMillis() - tickBegin;
    }

    public String getQueryHost(){
        try {
            Uri i = Uri.parse(queryUrl);
            return i.getHost();
        }catch (Exception e){
            Log.i(TAG,"getQueryHost",e);
        }
        return "unknow";
    }
    public long getQuerySlaps(){
        return liveQuerySlaps;
    }
    public String getStreamHost(){
        try {
            Uri i = Uri.parse(streamUrl);
            String h = i.getHost();
            String s = i.getScheme();
            String f = s + "://" + h;
            return f;
        }catch (Exception e){
            Log.i(TAG,"getStreamHost",e);
        }
        return "unknow";
    }
    public long getStreamSlaps(){
        return streamConnectSlaps;
    }

    public void clear()
    {
         retryQueryCount = 0;
         interupCount = 0;
         liveQuerySlaps = 0;
         streamConnectSlaps = 0;
         tickBegin = 0;
         queryUrl = "";
         streamUrl = "";

    }
    public void retryQeury(){
        Log.i(TAG,"retryQeury");
        retryQueryCount++;
    }
    public void interup(){
        Log.i(TAG,"interup");
        interupCount++;
    }

    public String toJsonString(String verName,String type)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("query", StreamStatitics.shareInstance().queryUrl);
            json.put("stream", StreamStatitics.shareInstance().streamUrl);
            json.put("type",type);
            json.put("querySlaps", StreamStatitics.shareInstance().getQuerySlaps());
            json.put("streamSlaps", StreamStatitics.shareInstance().getStreamSlaps());
            json.put("ver","" + verName);
            json.put("dev","android");
            String ll = json.toString();
            return ll;
        }catch (Exception e){}
        return null;
    }

    public void sendAudio(int dataSize){
        _audioFreq.triger(dataSize);
    }

    public void sendVideo(int dataSize){
        _videoFreq.triger(dataSize);
    }

    public String dataSpeedInfo(){
        String a = "audio: " + (int)(_audioFreq.getFreq()/1024.0) + "Kb/s\r\n";
        String v = "video: " + (int)(_videoFreq.getFreq()/1024.0) + "Kb/s\r\n";
        return (a + v);
    }

    private FreqStatitics _audioFreq = new FreqStatitics();
    private FreqStatitics _videoFreq = new FreqStatitics();

    @Override
    public String toString() {
        return "StreamStatitics{\n" +
                "retryQueryCount=" + retryQueryCount +
                ", \ninterupCount=" + interupCount +
                ", \nliveQuerySlaps=" + liveQuerySlaps +
                ", \nstreamConnectSlaps=" + streamConnectSlaps +
                ", \nqueryUrl=" + queryUrl +
                ", \nstreamUrl=" + streamUrl +
                "\n}";
    }
}
