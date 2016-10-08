package com.streampublisher.player;


import android.os.Handler;
import android.util.Log;
import com.streampublisher.StreamStatitics;
import com.streampublisher.live.HttpRequest;
import org.json.JSONObject;


public class PlayerConnector implements HttpRequest.Listener {
    public static final String TAG = "PlayerConnector";


    public interface Listener
    {
        void onQuerytStreamUrlRet(String url);
        void onQueryError();
    }

    private String _url;
    private Handler _handler;
    private int _reconnectCount = 0;
    private boolean _discard = false;
    private HttpRequest _httpRequest;
    private Listener _listener;

    public PlayerConnector(Listener listener) {
        _handler = new Handler();
        _listener = listener;
    }

    public void connect(String url) {
        _url = url;
        doConnect();
    }

    public void discard() {
        _discard = true;
        _listener = null;
        HttpRequest request = _httpRequest;
        _httpRequest = null;
        if (request != null) {
            request.cancel();
        }
    }


    private void doConnect() {
        Log.d(TAG, "doConnect");
        if(_httpRequest != null){
            _httpRequest.cancel();
            _httpRequest = null;
        }
        _httpRequest = new HttpRequest(_url,this);
        StreamStatitics.shareInstance().beginLiveQuery(_url);
    }

    @Override
    public void onReponse(String response)
    {
        StreamStatitics.shareInstance().endLiveQuery();
        try {
            JSONObject json = new JSONObject(response);
            int retCode = json.getInt("result");
            if (retCode == 0) {
                final String url = json.optString("url");
                _handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (_listener != null){
                            _listener.onQuerytStreamUrlRet(url);
                        }
                    }
                });
            }else if (retCode == 404){
                _handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (_listener != null){
                            _listener.onQueryError();
                        }
                    }
                });
            }else{
                retry();
            }

        } catch (Exception e) {
            Log.i(TAG, "onResponse exception", e);
            retry();
        }
    }

    @Override
    public void onError(String err) {
        retry();
    }

    private void retry()
    {
        if (_reconnectCount > 5){
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (_listener != null){
                        _listener.onQueryError();
                    }
                }
            });
            return;
        }

        _reconnectCount++;
        _handler.post(new Runnable() {
            @Override
            public void run() {
                if (_discard){
                    return;
                }
                doConnect();
            }
        });
    }

}
