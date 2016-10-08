package com.streampublisher.live;



import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class HttpRequest implements LiveRequest {

    public interface Listener
    {
        void onReponse(String string);
        void onError(String err);
    }
    public static final String TAG = "HttpLiveRequest";
    private Thread _thread;
    private HttpURLConnection _connection;
    private Listener _listener;

    public HttpRequest(String url, Listener listener)  {
        _listener = listener;
        URL u = null;
        try {
            u = new URL(url);
        }catch (Exception e){

        }
        _thread = new Thread(new RunnableDelegate(u));
        _thread.start();
    }

    @Override
    public void cancel() {

        synchronized (_thread) {
            if (_listener != null) {
                _listener = null;
            }
        }
    }

    private class RunnableDelegate implements Runnable {
        private URL _url;

        RunnableDelegate(URL url) {
            _url = url;
        }

        @Override
        public void run() {
            try {
                HttpURLConnection connection = (HttpURLConnection) _url.openConnection();
                synchronized (_thread) {
                    _connection = connection;
                }
                connection.setRequestMethod("POST");
                connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.addRequestProperty("Content-Length", "0");
                connection.setUseCaches(false);
                connection.setDoOutput(false);
                connection.connect();
                int resCode = connection.getResponseCode();
                Log.d(TAG, "rescode:" + resCode);
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {

                    synchronized (_thread) {
                        if (_listener != null) {
                            _listener.onError("Response code: " + connection.getResponseCode());
                        }
                    }
                } else {
                    InputStream input = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(input);
                    char[] buffer = new char[1024];
                    int offset = 0;
                    while (offset < buffer.length) {
                        int readed = reader.read(buffer, offset, buffer.length - offset);
                        if (readed == -1) {
                            break;
                        }
                        offset += readed;
                    }
                    String response = String.copyValueOf(buffer, 0, offset);
                    synchronized (_thread) {
                        if (_listener != null) {
                            _listener.onReponse(response);
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "exception:" + e.toString());
                synchronized (_thread) {
                    if (_listener != null) {
                        _listener.onError(e.getMessage());
                    }
                }
            }
            _connection = null;
        }
    }
}
