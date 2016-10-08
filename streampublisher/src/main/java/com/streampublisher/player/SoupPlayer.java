package com.streampublisher.player;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import sdk.miraeye.net.PlaySession;
import sdk.miraeye.player.EventListener;
import sdk.miraeye.player.NetStatusEvent;
import sdk.miraeye.player.NetStream;
import sdk.miraeye.player.Video;


public class SoupPlayer implements Player{
    public static final String TAG = "SoupPlayer";

    private NetStream _stream;
    private Video _video;
    private Surface _surface;
    private EventDelegate _playStateListener;
    private PlayerEvent _callbackListener;
    private static final int DEFAULT_BUFFER_TIME = 1000;
    private static final int DEFAULT_BUFFER_TIME_MAX = 15000;

    public SoupPlayer(Context context) {

    }

    @Override
    public void setSurface(Surface surface) {
        _surface = surface;
        if (_video != null)
        {
            _video.setSurface(surface);
        }
    }

    @Override
    public void setListener(PlayerEvent listener)
    {
        _callbackListener = listener;
    }

    @Override
    public long getCurrentPos()
    {
        return 0;
    }
    @Override
    public long getDuration()
    {
        return 0;
    }
    @Override
    public void seekTo(long positionMs) {

    }
    @Override
    public Boolean play(Uri uri)
    {
        if (_surface == null || _stream != null){
            Log.d(TAG,"play error!!!" + uri.toString());
            return false;
        }

        int protocol = PlaySession.PROTOCOL_RTMP;
        if (uri.getScheme().equals("soup")) {
            protocol = PlaySession.PROTOCOL_SOUP;
        }
        _stream = NetStream.create(protocol);
        _stream.setBufferTime(DEFAULT_BUFFER_TIME);
        _stream.setBufferTimeMax(DEFAULT_BUFFER_TIME_MAX);
        _video = Video.create();
        _video.setSurface(_surface);
        _video.attachNetStream(_stream);
        try {
            Log.d(TAG,"play" + uri.toString());
            _playStateListener = new EventDelegate();
            _stream.addEventListener(NetStatusEvent.class, _playStateListener);
            _stream.play(uri);
        }catch (Exception e){
            Log.e(TAG, "play exception", e);
            return false;
        }
        return true;
    }

    @Override
    public void release() {
        Log.d(TAG,"release");
        if (_playStateListener != null){
            _playStateListener.discard();
            _playStateListener = null;
        }
        if(_stream != null){
            _stream.close();
            _stream = null;
        }
        if (_video != null) {
            _video.dispose();
            _video = null;
        }

        _surface = null;
    }

    private class EventDelegate implements EventListener {
        protected Boolean _discard = false;
        public void discard(){
            _discard = true;
        }
        @Override
        public void onEvent(Object event) {
            NetStatusEvent info = (NetStatusEvent) event;
            Log.d(TAG,"EventDelegate," + info.code());
            switch (info.code()) {
                case "NetStream.Buffer.Empty":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (_discard){return;}
                            Log.d(TAG, "缓冲中！");
                            setState(PlayerEvent.STATE_LOADING_BUFFER);
                        }
                    });
                    break;
                case "NetStream.Buffer.Full":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (_discard){return;}
                            //Log.d(TAG, "播放就绪！");
                            setState(PlayerEvent.STATE_READY);
                        }
                    });
                    break;
                case "NetStream.Video.DimensionChange":
                    setState(PlayerEvent.STATE_READY);
                    break;
                case "NetStream.Connect.Success":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (_discard){return;}
                            Log.d(TAG, "已连接服务器...");
                            //setState(PlayerEvent.STATE_READY);
                        }
                    });
                    break;
                case "NetStream.Connect.Close":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (_discard){return;}
                            Log.d(TAG, "播放结束！");
                            setState(PlayerEvent.STATE_CONNECT_ERR);
                        }
                    });
                    break;
            }
        }
    }

    private void setState(int state)
    {
        if (_callbackListener != null){
            _callbackListener.onPlayerEvent(state);
        }
    }
    private void runOnUiThread(Runnable runnable)
    {
        _handler.post(runnable);
    }
    private Handler _handler = new Handler();
}