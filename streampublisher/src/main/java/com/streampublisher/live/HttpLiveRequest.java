package com.streampublisher.live;


import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpLiveRequest implements LiveRequest {
    public static final String TAG = "HttpLiveRequest";
    private Thread _thread;
    private HttpURLConnection _connection;
    private LiveClient.ResultListener _listener;

    HttpLiveRequest(String url, LiveClient.ResultListener listener) throws IOException {
        _listener = listener;
        _thread = new Thread(new RunnableDelegate(new URL(url)));
        _thread.start();
    }

    @Override
    public void cancel() {
        _thread.interrupt();
        synchronized (this) {
            if (_connection != null) {
               // _connection.disconnect();
                _connection = null;
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
                synchronized (this) {
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

                    synchronized (this) {
                        if (_connection != null) {
                            _listener.onLiveQueryFail("Response code: " + connection.getResponseCode());
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
                    Log.i(TAG, response);
                    JSONObject json = new JSONObject(response);
                    synchronized (this) {
                        if (_connection != null) {
                            _listener.onLiveQuerySuccess(json.getString("url"), json.getString("token"));
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                Log.d(TAG, "exception:" + e.toString());
                synchronized (this) {
                    if (_connection != null) {
                        _listener.onLiveQueryFail(e.getMessage());
                    }
                }
            }
            _connection = null;
        }
    }
}
