package com.streampublisher;


import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.util.log.LogUtil;
import com.google.android.exoplayer.util.Util;
import com.streampublisher.player.ExoPlayerWrap;
import com.streampublisher.player.Player;
import com.streampublisher.player.PlayerConnector;
import com.streampublisher.player.PlayerEvent;
import com.streampublisher.player.SoupPlayer;

import java.util.ArrayList;


public class StreamPlayer
{
    public static int MEDIA_TYPE_HLS = 0;
    public static int MEDIA_TYPE_UNKNOW = 1;

    public static final String TAG = "StreamPlayer";

    public void init(Context context,SurfaceView surface) {

        LogUtil.d(TAG,"init");
        _context = context;
        _videoSurfaceView = surface;
        _handler = new Handler();
        _waittingFirstReady = true;

        _videoSurface = _videoSurfaceView.getHolder().getSurface();
        _videoSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                LogUtil.d(TAG, "surfaceCreated");

                if (_player != null) {
                    _videoSurface = holder.getSurface();
                    _player.setSurface(_videoSurface);
                    if (_playUrl != null) {
                        Uri uri = Uri.parse(_playUrl);
                        _player.setListener(_playStateListener);

                        _player.play(uri);
                        _playUrl = null;
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                LogUtil.d(TAG, "surfaceChanged,fmt:%d,%dx%d", format, width, height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                LogUtil.d(TAG, "surfaceDestroyed");
                _videoSurface = null;
                if (_player != null) {
                    _player.setSurface(null);
                }
            }
        });

    }

    private PlayerEvent _listener;
    public void setListener(PlayerEvent listener) {
        _listener = listener;
    }


    private String _defaultProtocol = "rtmp";
    public void setProtocolList(String[] protocol)
    {

        _protocol.clear();
        for (int i=0;i<protocol.length;i++)
        {
            if ( i == 0){
                _defaultProtocol = protocol[i];
            }
            _protocol.add(protocol[i]);
            LogUtil.d(TAG, "setProtocolList: " + protocol[i]);
        }


    }

    public void setSourceList(String sources)
    {
        _sourceUrl = sources;

    }

    public void connnect(String playUrl) {

        LogUtil.d(TAG,"connnect:%s",playUrl);
        _playUrl = playUrl;
        _playUrl_bak = playUrl;
        setState(PlayerEvent.STATE_CONNECTING);
        if (_playUrl == null) {
            queryStream();
        }else{
            play(_playUrl);
        }

    }

    void queryStream()
    {
        if (_playerConnector != null){
            _playerConnector.discard();
            _playerConnector = null;
        }

        if (_sourceUrl == null){
            LogUtil.d(TAG,"queryStream,directly play url_bak:%s",_playUrl_bak);
            play(_playUrl_bak);
        }else {
            _playerConnector = new PlayerConnector(new PlayerConnector.Listener() {
                @Override
                public void onQuerytStreamUrlRet(String url) {
                    LogUtil.r(TAG, "onQuerytStreamUrlRet," + url);
                    play(url);
                }

                @Override
                public void onQueryError() {
                    _handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (_gc) {
                                return;
                            }
                            if (_retryCount > MAX_RETRY_COUNT) {
                                if (_listener != null) {
                                    _listener.onPlayerEvent(PlayerEvent.STATE_CONNECT_ERR);
                                    LogUtil.r(TAG, "onQueryError,close");
                                    close();
                                }
                            } else {
                                LogUtil.r(TAG, "onQueryError,restart");
                                removeFrezonCheck();
                                restart();
                            }
                        }
                    }, 1000);

                }
            });
            String url = chooseSourceUrl();
            _playerConnector.connect(url);
        }
    }

    public long getCurrentPos()
    {
        if (_player != null )
        {
            return _player.getCurrentPos();
        }
        return 0;
    }

    public void seekToPos(long pos){
        if(_player != null){
            _player.seekTo(pos);
        }
    }

    public long getDuration()
    {
        if (_player != null )
        {
            return _player.getDuration();
        }
        return 0;
    }

    long _lastUpdateProtocol = 0;
    String _lastProtocol = "";
    private void evalProtocol()
    {
        if (TextUtils.isEmpty(_lastProtocol)){
            return;
        }

        long slaps = System.currentTimeMillis() - _lastUpdateProtocol;
        _lastUpdateProtocol = System.currentTimeMillis();

        //播放10秒都不到?应该很差,干掉
        int th = 10 * 1000;
        if (slaps < 10 * 1000){
            for (int i=0; i<_protocol.size();i++){
                if (_lastProtocol.compareToIgnoreCase(_protocol.get(i)) == 0){
                    _protocol.remove(i);
                    LogUtil.w(TAG, "evalProtocol,play less %dms,remove:%s", th,_lastProtocol);
                    return;
                }
            }
        }

        LogUtil.d(TAG,"evalProtocol,ingore");


//        Uri u = Uri.parse(url);
//        String s = u.getScheme();
//        int p = u.getPort();
//        if (p <= 0 )p = 80;
//        int slaps = Ping.getPingCache(u.getHost(), p);
//        LogUtil.d(TAG, "trackUrl," + slaps + "ms");
//        if (slaps > 100 || slaps < 0){
//            for (int i=0; i<_protocol.size();i++){
//                if (s.compareToIgnoreCase(_protocol.get(i)) == 0){
//                    _protocol.remove(i);
//                    if (slaps < 500) {
//                        _protocol.add(s);
//                        LogUtil.w(TAG, "trackUrl,remove to tail:%s ", s);
//                    }else{
//                        LogUtil.w(TAG,"trackUrl,remove %s", s);
//                    }
//
//                    break;
//                }
//            }
//        }
    }

    private String chooseSourceUrl()
    {
        evalProtocol();

        String p = (_protocol.isEmpty() ? _defaultProtocol : _protocol.get(0));
        if (TextUtils.isEmpty(p)){
            p = "rtmp";
        }
        _lastProtocol = p;
        String u = _sourceUrl + p;
        LogUtil.d(TAG, "chooseSourceUrl,%d|%s,%s", _protocol.size(),p,u);


        return u;
    }

    /*调用disConnect后,不要再使用这个对象,重新创建一个*/
    public void disConnect()
    {
        LogUtil.d(TAG, "disConnect");

        _listener = null;
        close();
        gc();
    }

    private Runnable _checkFrezonRunnable;
    private void play(String url) {

        LogUtil.r(TAG, "playUrl..." + url);
        //StreamStatitics.shareInstance().clear();
        StreamStatitics.shareInstance().beginStreamConnect(url);

        _playStateListener = new EventDelegate();

        if (url.indexOf("rtmp://") >= 0){
            _player = new SoupPlayer(_context);;//contentType = Util.TYPE_OTHER;
        }else {
            int contentType = Util.TYPE_OTHER;
            if(url.indexOf(".m3u8") >= 0) {
                contentType = Util.TYPE_HLS;
            }
            _player = new ExoPlayerWrap(contentType,_context);

        }

        if (_videoSurface != null){
            Uri uri = Uri.parse(url);
            _player.setSurface(_videoSurface);
            _player.setListener(_playStateListener);
            _player.play(uri);
            StreamStatitics.shareInstance().beginStreamConnect(url);

            _playUrl = null;
        }else{
            _playUrl = url;
        }


        removeFrezonCheck();

        _checkFrezonRunnable = new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG,"video frezon?!");
                _lastUpdateProtocol = System.currentTimeMillis();
                restart();
                _checkFrezonRunnable = null;
            }
        };
        _handler.postDelayed(_checkFrezonRunnable,6000);
        LogUtil.d(TAG, "start video frezon check");
    }

    private void close() {
        LogUtil.d(TAG, "close");
        _playUrl = null;

        removeFrezonCheck();

        if (_playerConnector != null){
            _playerConnector.discard();
            _playerConnector = null;
        }

        if (_playStateListener != null){
            _playStateListener.discard();
            _playStateListener = null;
        }
        if (_player != null) {
            _player.release();
            _player = null;
            LogUtil.d(TAG,"_player.release() close");
        }
    }

    private void removeFrezonCheck()
    {
        if (_checkFrezonRunnable != null){
            _handler.removeCallbacks(_checkFrezonRunnable);
            _checkFrezonRunnable = null;
        }
    }
    private void restart(){
        if (_gc){
            LogUtil.w(TAG,"restart,but gc called");
            return;
        }
        LogUtil.d(TAG, "restart:" + _retryCount);
        close();
        _retryCount++;

        setState(PlayerEvent.STATE_RE_CONNECTING);
        //_idx = (_idx + 1)%2;
        queryStream();
    }

    public void onResume() {

    }

    public void onPause() {

    }

    private void gc(){
        LogUtil.d(TAG,"gc");
        _gc = true;

        close();
        _context = null;
        _videoSurfaceView = null;
        //_handler = null;//do not set it null
    }

    private class EventDelegate implements PlayerEvent {
        protected Boolean _discard = false;
        public void discard(){
            _discard = true;
        }

        @Override
        public void onPlayerEvent(int event) {
            if (_discard){
                return;
            }
            setState(event);
        }
    }

    private void setState(final int state){
        _state = state;

        switch (_state)
        {
            case PlayerEvent.STATE_IDLE:
            case PlayerEvent.STATE_CONNECTING:
            case PlayerEvent.STATE_LOADING_BUFFER:
            case PlayerEvent.STATE_READY:
            case PlayerEvent.STATE_RE_CONNECTED:
            case PlayerEvent.STATE_RE_CONNECTING:
            {
                if (_state == PlayerEvent.STATE_READY){
                    LogUtil.d(TAG,"recv video ready state,remove frezon check");
                    removeFrezonCheck();

                    _retryCount = 0;
                    if (_listener != null) {
                        if (_waittingFirstReady) {
                            _waittingFirstReady = false;
                            StreamStatitics.shareInstance().endStreamConnect();
                            _listener.onPlayerEvent(PlayerEvent.STATE_READY);
                        }else
                        {
                            _listener.onPlayerEvent(PlayerEvent.STATE_RE_CONNECTED);
                        }
                    }
                }else {
                    if (_listener != null) {
                        _listener.onPlayerEvent(_state);
                    }
                }
            }
            break;
            case PlayerEvent.STATE_CONNECT_ERR:
            {
                LogUtil.d(TAG,"setState:STATE_CONNECT_ERR");
                _handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (_gc){return;}
                        if (_retryCount > MAX_RETRY_COUNT){
                            if (_listener != null)
                            {
                                _listener.onPlayerEvent(PlayerEvent.STATE_CONNECT_ERR);
                                close();
                            }
                        }else {
                            removeFrezonCheck();
                            restart();
                        }
                    }
                },1000);
            }
            break;
            case PlayerEvent.STATE_REPLAY:
                break;
        }
    }

    private static final int MAX_RETRY_COUNT = 4;
    private SurfaceView _videoSurfaceView;
    private Surface _videoSurface;

    private Player _player;

    private Context _context;
    private Handler _handler;
    private Boolean _gc = false;
    private String _playUrl,_playUrl_bak;
    private String _sourceUrl;
    private ArrayList<String> _protocol = new ArrayList<>();
    private boolean _waittingFirstReady = true;
    private int     _state = PlayerEvent.STATE_IDLE;
    private EventDelegate _playStateListener;
    private int _retryCount;
    private PlayerConnector _playerConnector;
}