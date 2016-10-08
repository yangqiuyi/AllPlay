package com.streampublisher.recorder;

import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaFormat;
import android.os.*;
import android.util.Log;
import android.view.SurfaceHolder;
import sdk.miraeye.codec.DataSink;
import sdk.miraeye.codec.MediaEncoder;
import sdk.miraeye.yuv.ImageTransform;



class VideoRecorder implements CameraCapture.FrameListener {

    private static final String TAG = "VideoRecorder";

    private final int mWidth;
    private final int mHeight;
    private CameraCapture mCameraCapture = null;
    private CameraHandlerThread cameraThread_ = null;

    private SurfaceHolder mSurfaceHolder;
    private int mRotation = ImageTransform.ROTATE_90;
    private boolean mHdEncoder = true;
    private int mActivity_rotation;
    public VideoRecorder(MediaFormat mediaFormat, DataSink sink,boolean hdEncoder,int activity_rotation) {

        Log.d(TAG, "VideoRecorder");
        mHdEncoder = hdEncoder;
        mActivity_rotation = activity_rotation;
        mWidth = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
        mHeight = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
        cameraThread_ = new CameraHandlerThread( mediaFormat,sink);

    }

    int mCameraId = -1;
    public boolean startPreview(int cameraId, SurfaceHolder surfaceHolder)
    {
        if (mCameraId == cameraId){
            return true;
        }
        Log.d(TAG, "startPreview");
        mCameraCapture = new CameraCapture();
        mSurfaceHolder = surfaceHolder;
        int retCameraId = mCameraCapture.startPreview(cameraId,mActivity_rotation, surfaceHolder, mWidth, mHeight);
        if (retCameraId < 0){
            return false;
        }
        mCameraId =retCameraId;
        int rotation = mCameraCapture.getCameraRatation();
        mRotation = calcVideoRotaion(rotation);
        cameraThread_.sendMessage(MSG_PREPARE, null, mWidth, mHeight);
        return true;
    }

    private int calcVideoRotaion(int cameraRotation)
    {
        int r = 0;
        if (cameraRotation == 0){
            r = ImageTransform.ROTATE_0;
        }else if (cameraRotation == 90){
            r = ImageTransform.ROTATE_90;
        }else if (cameraRotation == 180){
            r = ImageTransform.ROTATE_180;
        }else {
            r = ImageTransform.ROTATE_270;
        }

        return r;
    }

    public boolean switchCameraId(int newCameraId)
    {
        if (mCameraId == newCameraId){
            return true;
        }
        if (mCameraCapture == null){
            return false;
        }

        Log.d(TAG, "switchCameraId:" + newCameraId);

        mCameraCapture.stopPreview();

        int cameraId = mCameraCapture.startPreview(newCameraId,mActivity_rotation, mSurfaceHolder, mWidth, mHeight);
        if (cameraId < 0){
            return false;
        }
        mCameraId = cameraId;
        int rotation = mCameraCapture.getCameraRatation();
        int newRotation = calcVideoRotaion(rotation);
        if (mRotation != newRotation) {
            mRotation = newRotation;
            cameraThread_.sendMessage(MSG_ROTATION);
        }
        return true;
    }

    public void stopPreview() {
        Log.d(TAG, "stopPreview");
        mCameraCapture.stopPreview();
        mCameraCapture = null;
    }

    public void startCapture() {
        Log.d(TAG, "startCapture");
        mCameraCapture.setPreviewCapture(this);
    }

    public void stopCapture() {
        Log.d(TAG, "stopCapture");
        mCameraCapture.setPreviewCapture(null);
    }

    public void discard() {
        Log.d(TAG, "discard");

        if (mCameraCapture != null) {
            mCameraCapture.setPreviewCapture(null);
            mCameraCapture.stopPreview();
            mCameraCapture = null;
        }

        cameraThread_.sendMessage(MSG_QUIT);
        cameraThread_ = null;

    }

    public Boolean openLamp(Boolean open) {
        if (mCameraCapture != null) {
            return mCameraCapture.openLamp(open);
        }
        return false;
    }

    public void cameraFocus() {
        {
//            if (mCameraCapture != null) {
//                mCameraCapture.cameraFocus();
//            }
        }
    }

    private static final int MSG_ON_FRAME = 1;
    private static final int MSG_PREPARE = 2;
    private static final int MSG_ROTATION = 3;
    private static final int MSG_QUIT = 4;


    @Override
    public void onFrame(CameraCapture.CameraCaptureCallback capture, int width, int height) {
        //Log.d(TAG,"onFrame,%dx%d",width,height);
        cameraThread_.sendMessage(MSG_ON_FRAME, capture, width, height);

    }


    private class CameraHandlerThread extends HandlerThread {
        //public static final String TAG = "CameraHandlerThread";
        private CameraHandler handler_ = null;
        private MediaEncoder _videoEncoder;
        private DataSinkWrap _flvDataSink;

        private ImageTransform _transform;
        private byte[] _transformCache;
        private byte[] _transformOutput;
        private MediaFormat mVideoFormat;

        private int _lastFrameWidth = 0;
        private int _lastFrameHeight = 0;
        private DataSink _sink;
        CameraHandlerThread(MediaFormat mediaFormat, DataSink sink) {
            super("CameraHandlerThread");
            start();

            _sink = sink;
            mVideoFormat = mediaFormat;

        }

        public void sendMessage(int what) {
            sendMessage(what, null, 0, 0);
        }

        public void sendMessage(int what, Object obj, int arg1, int arg2) {
            if (handler_ == null) {
                handler_ = new CameraHandler(CameraHandlerThread.this.getLooper());
            }

            Message msgMessage = handler_.obtainMessage();
            msgMessage.what = what;
            msgMessage.obj = obj;
            msgMessage.arg1 = arg1;
            msgMessage.arg2 = arg2;
            handler_.sendMessage(msgMessage);
        }

        private class CameraHandler extends Handler {

            public CameraHandler(Looper looper) {
                super(looper);
            }

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_ON_FRAME: {
                        onFrame((CameraCapture.CameraCaptureCallback) msg.obj, msg.arg1, msg.arg2);
                        break;
                    }
                    case MSG_PREPARE: {
                        prepareEncoder(msg.arg1, msg.arg2);
                        break;
                    }

                    case MSG_ROTATION: {
                        resetTransform();
                        break;
                    }

                    case MSG_QUIT: {
                        discard();
                        getLooper().quit();
                        break;
                    }
                }
            }

            private void prepareEncoder(int captureWidth, int captureHeight)
            {

                _flvDataSink = new DataSinkWrap(_sink);

                _lastFrameWidth = captureWidth;
                _lastFrameHeight = captureHeight;

                MediaEncoder videoEncoder = null;
                if (mHdEncoder) {
                    videoEncoder = MediaEncoder.create(MediaEncoder.HD_VIDEO_ENCODER, _flvDataSink);
                } else {
                    videoEncoder = MediaEncoder.create(MediaEncoder.AV_VIDEO_ENCODER, _flvDataSink);
                }
                _videoEncoder = videoEncoder;


                try {
                    if (mRotation == ImageTransform.ROTATE_0 ||
                            mRotation == ImageTransform.ROTATE_180) {
                        mVideoFormat.setInteger(MediaFormat.KEY_WIDTH, captureWidth);
                        mVideoFormat.setInteger(MediaFormat.KEY_HEIGHT, captureHeight);
                    }else if (mRotation == ImageTransform.ROTATE_90 ||
                            mRotation == ImageTransform.ROTATE_270)
                    {
                        mVideoFormat.setInteger(MediaFormat.KEY_WIDTH, captureHeight);
                        mVideoFormat.setInteger(MediaFormat.KEY_HEIGHT, captureWidth);
                    }
                    _videoEncoder.prepare(mVideoFormat);

                } catch (Exception e) {
                    Log.d(TAG, "prepareEncoder", e);
                    _videoEncoder.release();
                    _videoEncoder = null;
                    return;
                }
                resetTransform();
            }
            private void resetTransform()
            {
                //ImageTransform.COLOR_FORMAT_NV21,
                _transform = new ImageTransform(
                        _videoEncoder.colorFormat(),
                        new Point(_lastFrameWidth, _lastFrameHeight),
                        new Rect(0, 0, mVideoFormat.getInteger(MediaFormat.KEY_WIDTH),
                                mVideoFormat.getInteger(MediaFormat.KEY_HEIGHT)),
                        mRotation
                );
                _transformCache = new byte[_transform.getCacheBytes()];
                _transformOutput = new byte[_transform.getOutputBytes()];


            }

            private void destroyEncoder()
            {
                Log.w(TAG,"destroyEncoder");
                if (_flvDataSink != null){
                    _flvDataSink.discard();
                    _flvDataSink = null;
                }

                _transform = null;
                _transformCache = null;
                _transformOutput = null;

                if (_videoEncoder != null) {
                    _videoEncoder.release();
                    _videoEncoder = null;
                }
            }

            private void discard() {

                Log.w(TAG,"discard");
                destroyEncoder();

            }

            private int _frameCount = 0;
            private void onFrame(CameraCapture.CameraCaptureCallback capture, int width, int height) {
               // Log.d(TAG,"CameraHandler onFrame,%dx%d",width,height);

                if(_lastFrameWidth != width || _lastFrameHeight != height){
                    Log.w(TAG, "CameraCapture size chaged");
                    destroyEncoder();
                    prepareEncoder(width,height);
                }
                if (_videoEncoder != null) {
                    doOnFrame(capture.data(), width, height);
                }else{
                    Log.d(TAG, "onFrame,but videoEncoder null");
                }
                capture.finish();
            }

            private void doOnFrame(byte[] frameData, int width, int height) {
                try {
                    //Log.d(TAG,"doOnFrame,sending");
                    if (frameData == null )
                    {
                        Log.w(TAG,"doOnFrame empty");
                        return;
                    }

                    _transform.transform(frameData, _transformOutput, _transformCache);
                    _videoEncoder.write(System.currentTimeMillis(), _transformOutput);
                    //_videoEncoder.write(System.currentTimeMillis(), frameData);

                    if (_frameCount < 10){
                        Log.d(TAG, "doOnFrame,recv frame: " + _frameCount + "," + frameData.length);
                    }
                    _frameCount++;

                } catch (Exception e) {
                    Log.i(TAG, "onFrame", e);
                }
            }

        }
    }

    class DataSinkWrap implements DataSink {
        DataSink _sink;

        public DataSinkWrap(DataSink sink) {
            _sink = sink;
        }

        public void discard() {
            _sink = null;
        }

        @Override
        public void write(MediaFormat mediaFormat, int i, long l, byte[] bytes) {
            if (_sink != null) {
                _sink.write(mediaFormat, i, l, bytes);
            }
        }

        @Override
        public void flush() {
            if (_sink != null) {
                _sink.flush();
            }
        }
    }
}