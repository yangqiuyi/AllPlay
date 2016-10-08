package com.streampublisher.recorder;


import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.streampublisher.StreamStatitics;
import com.streampublisher.live.LiveClient;
import com.streampublisher.live.LiveRequest;

import sdk.miraeye.net.PublishSession;


public class PublishConnector implements LiveClient.ResultListener, PublishSession.Listener {
    public static final String TAG = "PublishConnector";

    private PublishSession _session;
    private LiveRequest _request;
    private PublishListener _callback = null;
    private Boolean _bConnected = false;

    private String _url;
    private String _streamName;
    private String _streamClient;
    private String _streamSession;
    private Handler _handler;
    private int _reconnectCount = 0;
    private boolean _discard = false;
    private boolean _firstConnect = true;

    public PublishConnector(PublishListener callback) {
        _callback = callback;
        _handler = new Handler();
    }

    public void connect(String url, String streamName, String session) {
        _url = url;
        _streamName = streamName;
        _streamClient = streamName;
        _streamSession = session;

        doConnect();
    }

    public void discard() {
        _discard = true;
        _bConnected = false;
        _callback = null;

        LiveRequest request = _request;
        _request = null;
        if (request != null) {
            request.cancel();
        }

        synchronized (this) {
            PublishSession session = _session;
            _session = null;
            if (session != null) {
                session.close();
            }
        }
        Log.d(TAG, "discard");
    }

    public void sendAudio(long timestamp, byte[] data, long lifetime) {
        if (!_bConnected) {
            return;
        }

        synchronized (this) {
            _session.sendAudio(timestamp, data, lifetime);
        }
    }

    public void sendVideo(long timestamp, byte[] data, long lifetime) {
        if (!_bConnected) {
            return;
        }
        synchronized (this) {
            _session.sendVideo(timestamp, data, lifetime);
        }
    }

    private void doConnect() {
        Log.d(TAG, "doConnect");
        LiveClient liveClient = new LiveClient();
        try {
            StreamStatitics.shareInstance().beginLiveQuery(_url);
            LiveRequest request = liveClient.publish(_url, this);
            _request = request;
        } catch (Exception e) {
            Log.i(TAG, "doConnect exception", e);
        }
    }

    @Override
    public void onLiveQuerySuccess(String url, String token) {

        _reconnectCount = 0;
        if (_request == null) {
            return;
        }
        StreamStatitics.shareInstance().endLiveQuery();
        String t = url + "?token=" + token;
        Uri uri = Uri.parse(t);

        Log.i(TAG, "onLiveQuerySuccess:" + t);

        int protocol = PublishSession.PROTOCOL_RTMP;
        if (uri.getScheme().equals("soup")) {
            protocol = PublishSession.PROTOCOL_SOUP;
        }
        try {
            StreamStatitics.shareInstance().beginStreamConnect(t);
            PublishSession publish = PublishSession.create(protocol, uri, this);
            _session = publish;
        } catch (Exception e) {
            Log.i(TAG, "PublishSession.create exception", e);
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (_callback != null && !_discard) {
                        _callback.onPublishError();
                    }
                }
            });
        }
    }

    @Override
    public void onLiveQueryFail(String reason) {
        if (_request == null) {
            return;
        }
        _request = null;
        Log.d(TAG, "onLiveQueryFail:" + reason);
        doReconnect();
    }

    @Override
    public void onConnected(PublishSession publishSession) {
        _bConnected = true;
        boolean firstConnect = _firstConnect;
        _firstConnect = false;
        Log.d(TAG, "publishSession connected.");

        if (_callback != null) {
            if (firstConnect) {
                StreamStatitics.shareInstance().endStreamConnect();
                _callback.onPublishConnected();
            } else {
                StreamStatitics.shareInstance().interup();
                _callback.onPublishReconnected();
            }
        }
    }

    @Override
    public void onClose(PublishSession publishSession) {

        Log.d(TAG, "publishSession closed.");
        _bConnected = false;

        synchronized (this) {
            PublishSession session = _session;
            _session = null;
            if (session != null) {
                session.close();
            }
        }
        doReconnect();

    }

    private void doReconnect() {
        if (_discard) {
            return;
        }
        _handler.post(new Runnable() {
            @Override
            public void run() {
                if (_discard) {
                    return;
                }
                if (_reconnectCount < 10) {
                    if (!_firstConnect) {
                        if (_callback != null) {
                            _callback.onPublishReconnecting();
                        }
                    }
                    Log.d(TAG, "doReconnect");
                    doConnect();
                } else {
                    if (_callback != null) {
                        Log.d(TAG, "onPublishError");
                        _callback.onPublishError();
                    }
                }
            }
        });
    }
}
