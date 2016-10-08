package com.streampublisher;


import android.app.Activity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.streampublisher.recorder.MediaRecord;
import com.streampublisher.recorder.PublishListener;

public class StreamPublisher {
    private static final String TAG = "StreamPublisher";

    private SurfaceView _surfaceView;

    private MediaRecord mediaRecord_ = null;

    private String _url;
    private String _streamName ;
    private String _sessionId;
    private Activity _context;

    public static boolean VIDEO_HD_ENCODER = true;
    public static int DEFAULT_CAMERA_ID = 1;
    public void init(Activity context,String  streamName, String url, String sessionId) {

        _context = context;
        _url = url;
        _streamName = streamName;
        _sessionId = sessionId;
        cameraId = (DEFAULT_CAMERA_ID % 2);
    }

    public void switchCamera() {
        cameraId = (cameraId + 1) % 2;
        Log.d(TAG, "switch cameraId:" + cameraId);
        if (mediaRecord_ != null) {
            mediaRecord_.switchCameraId(cameraId);
        }
    }

    private Boolean cameraLamp = false;

    public void switchCameraLamp() {
        if (mediaRecord_ != null) {
            cameraLamp = !cameraLamp;
            mediaRecord_.openLamp(cameraLamp);
        }
    }

    private int WIDTH = 640;
    private int HEIGHT = 480;

    public void setSurfaceView(SurfaceView surfaceView) {
        _surfaceView = surfaceView;

        _surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(TAG, "surfaceDestroyed");
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "surfaceCreated, width:" + _surfaceView.getWidth() + ",height:" + _surfaceView.getHeight());
                if (mediaRecord_ == null) {
                    int rotation = _context.getWindowManager().getDefaultDisplay().getRotation();
                    mediaRecord_ = new MediaRecord(rotation,WIDTH, HEIGHT);
                    doConnect();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                Log.d(TAG, "surfaceChanged, width:" + width + ",height:" + height + ",format:" + format);
            }
        });
    }


    public void connect() {
        Log.d(TAG, "connect begin");

        doConnect();
    }

    public void disconnect() {
        if (mediaRecord_ != null) {
            Log.d(TAG, "disconnect");
            mediaRecord_.stopPublish();
        }
    }

    private void doConnect() {


        if (mediaRecord_ == null) {
            Log.d(TAG, "doConnect,waiting for surface create...");
            return;
        }

        String url = _url;
        mediaRecord_.prepare(cameraId,_surfaceView.getHolder());
        mediaRecord_.startPublish(url, _streamName, _sessionId, _listener);
    }

    private int cameraId = 1;

    public void onResume() {
        Log.d(TAG, "onResume");
    }

    public void onPause() {
        Log.d(TAG, "onPause");
    }

    private PublishListener _listener;

    public void setListener(PublishListener listener) {
        _listener = listener;
    }


}