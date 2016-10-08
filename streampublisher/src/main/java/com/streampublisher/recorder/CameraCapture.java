package com.streampublisher.recorder;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.util.log.LogUtil;
import com.streampublisher.CamParaUtil;

import java.util.List;


public class CameraCapture implements Camera.AutoFocusCallback {

    public interface CameraCaptureCallback
    {
        byte[] data();
        void finish();
    }
    public interface FrameListener {
        void onFrame(CameraCaptureCallback capture, int width, int height);

    }

    private static final String TAG = "CameraCapture";
    private String flashMode_ = Camera.Parameters.FLASH_MODE_OFF;
    private Camera mCamera = null;
    private FrameListener listener_ = null;

    private int mPreferWidth = 0;
    private int mPreferHeight = 0;
    private int mCaptureWidth = 0;
    private int mCaptureHeight = 0;
    private int mFrameCount = 0;
    private SurfaceCallback mSurfaceCallback = null;
    public CameraCapture() {

    }

    //private int mRotation = 90;
    private int mActivityRotation;
    private int mCameraRatation = 90;
    private int mCameraId = 0;
    private boolean mPreviewStarted = false;
    public int startPreview(int cameraId,int activity_rotation,SurfaceHolder surface, int preferWidth, int preperHeight) {

        LogUtil.d(TAG, "startPreview");

        mPreferWidth = preferWidth;
        mPreferHeight = preperHeight;
        mActivityRotation = activity_rotation;
        cameraId = startCamera(cameraId);
        try {
            if (cameraId >= 0) {
                mSurfaceCallback = new SurfaceCallback();
                surface.addCallback(mSurfaceCallback);
                if (surface.getSurface() != null) {
                    mCamera.setPreviewDisplay(surface);
                }
            }
        } catch (Exception e) {
            LogUtil.c(TAG,"startPreview",e);
            mCamera.release();
            mCamera = null;
            return -1;
        }
        mPreviewStarted = true;
        return cameraId;
    }

    private int startCamera(int cameraId)
    {
        LogUtil.d(TAG,"startCamera");
        try {
            Camera camera = Camera.open(cameraId);
            mCameraId = cameraId;
            mCamera = camera;
        }catch (Exception e){
            LogUtil.c(TAG,"startPreview exception",e);
            return -1;
        }

        CamParaUtil.printSupportPreviewSize(mCamera);
        Camera.Size size = CamParaUtil.getPropPreviewSize(mCamera, mPreferWidth, mPreferHeight);
        LogUtil.d(TAG, "set camera preview size:" + size.width + "x" + size.height);
        mCaptureWidth = size.width;
        mCaptureHeight = size.height;

        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewSize(mCaptureWidth, mCaptureHeight);

        params.setPreviewFormat(ImageFormat.YV12);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null
                && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        mCamera.setParameters(params);

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, cameraInfo);
        mCameraRatation = cameraInfo.orientation;
        int rotation = CamParaUtil.determineDisplayOrientation(mCameraId, mCameraRatation, mActivityRotation);
        mCamera.setDisplayOrientation(rotation);

        _captureDataHelper = new CameraCaptureWraper(mCamera);
        mCamera.startPreview();

        return cameraId;
    }

    private void stopCamera()
    {
        LogUtil.d(TAG,"stopCamera");
        if (_captureDataHelper != null){
            _captureDataHelper.discard();
            _captureDataHelper = null;
        }

        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private CameraCaptureWraper _captureDataHelper;
    public void stopPreview()
    {
        LogUtil.d(TAG, "stopPreview");

        if (mSurfaceCallback != null){
            mSurfaceCallback.discard();
            mSurfaceCallback = null;
        }
        stopCamera();
        mPreviewStarted = false;
    }

    public int getCameraRatation()
    {
        return mCameraRatation;
    }

    public Boolean openLamp(Boolean open) {
        if (mCamera == null) {
            return false;
        }
        Boolean success = true;
        Camera.Parameters parameters = mCamera.getParameters();
        if (open) {
            final List<String> flashModes = parameters.getSupportedFlashModes();
            if (flashModes != null && flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                flashMode_ = Camera.Parameters.FLASH_MODE_TORCH;
            } else {
                success = false;
            }
        } else {
            flashMode_ = Camera.Parameters.FLASH_MODE_OFF;
        }
        if (success && parameters.getFlashMode() != flashMode_) {
            parameters.setFlashMode(flashMode_);
            mCamera.setParameters(parameters);
        }
        return success;
    }

    private EmptyFrameComposer mEmptyFrameComposer;
    class EmptyFrameComposer
    {
        private Handler  mHandler = new Handler();
        private Runnable mEmptyFrameComposer;
        private EmptyFrameCapture mEmptyCapture;
        void startEmptyFrameTrack()
        {
            if (!mPreviewStarted){
                return;
            }
            if (listener_ == null){
                return;
            }
            LogUtil.d(TAG,"startEmptyFrameTrack");
            mEmptyCapture = new EmptyFrameCapture();
            mEmptyFrameComposer = new Runnable() {
                @Override
                public void run() {
                    if (mEmptyFrameComposer == null || listener_ == null ||
                            mEmptyCapture == null){
                        LogUtil.d(TAG,"empty composer onframe ingore");
                        return;
                    }

                    LogUtil.d(TAG,"empty composer onframe");
                    listener_.onFrame(mEmptyCapture,mCaptureWidth,mPreferHeight);
                    mHandler.postDelayed(this,1000/10);
                }
            };
            mHandler.postDelayed(mEmptyFrameComposer,1000);
        }

        void stopEmptyFrameTrace()
        {
            LogUtil.d(TAG,"stopEmptyFrameTrace");
            mEmptyFrameComposer = null;
            mEmptyCapture = null;
        }

        class EmptyFrameCapture implements CameraCaptureCallback
        {
            private byte[] _data;

            public EmptyFrameCapture(){
                _data = new byte[mLastFrameDataSize];
            }

            @Override
            public byte[] data() {
                return _data;
            }

            @Override
            public void finish() {

            }
        }

    }

    public void setPreviewCapture(FrameListener listener)
    {
        listener_ = listener;
    }

    private int mLastFrameDataSize  = 0;
    class CameraCaptureWraper implements Camera.PreviewCallback,CameraCaptureCallback
    {
        private byte[] _data;
        private Camera _camera;
;
        public CameraCaptureWraper(Camera camera){
            _camera = camera;

            mLastFrameDataSize = (mCaptureWidth * mCaptureHeight * 3 / 2);
            for (int i = 0; i < 1; i++) {
                mCamera.addCallbackBuffer(new byte[mLastFrameDataSize]);
            }

            _camera.setPreviewCallbackWithBuffer(this);

        }
        public void setData(byte[] data){
            if (_discard){
                LogUtil.w(TAG,"CameraCaptureHandler setData,but discard...");
                return;
            }
            if (data == null){
                LogUtil.w(TAG,"CameraCaptureHandler setData,input data null...");
            }
            if (_data != null){
                LogUtil.w(TAG,"CameraCaptureHandler setData,but _data not null,maybe finish() not call...");
            }
            _data = data;
        }

        Boolean _discard = false;
        public void discard(){
            LogUtil.d(TAG,"CameraCaptureHandler discard()");
            synchronized (this)
            {
                _discard = true;
                _camera = null;
            }
        }

        @Override
        public byte[] data() {
            return _data;
        }

        @Override
        public void finish() {

           // LogUtil.d(TAG,"CameraCaptureHandler finsh in");
            synchronized (this){
                byte[] data = _data;
                _data = null;
                if (!_discard && _camera != null){

                    if (data == null){
                        LogUtil.e(TAG,"addCallbackBuffer null???");
                    }
                    _camera.addCallbackBuffer(data);
                }else{
                    LogUtil.w(TAG,"CameraCaptureHandler finsh, discard or camera null");
                }
            }
           // LogUtil.d(TAG, "CameraCaptureHandler finsh out");

        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            if (_discard){
                LogUtil.w(TAG, "onPreviewFrame,discard");
                return;
            }

            if (mFrameCount < 10){
                LogUtil.w(TAG, "onPreviewFrame,recv frame: " + mFrameCount + "," + data.length);
            }
            mFrameCount++;
            setData(data);

            if (listener_ != null) {
                listener_.onFrame(this,mCaptureWidth,mCaptureHeight);
            }else{
                LogUtil.d(TAG,"onPreviewFrame listener null");
            }

        }
    }

    @Override
    public void onAutoFocus(boolean arg0, Camera arg1) {
        Log.i(TAG, "foucus result:" + arg0);
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        private boolean _discard = false;
        public void discard(){
            _discard = true;
        }
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (_discard){
                return;
            }
            LogUtil.d(TAG, "surfaceCreated");
            try{
                if (mCamera == null){
                    startCamera(mCameraId);
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                    }
                }
            }catch (Exception e){}

            if (mEmptyFrameComposer != null){
                mEmptyFrameComposer.stopEmptyFrameTrace();
                mEmptyFrameComposer = null;
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (_discard){
                return;
            }
            LogUtil.d(TAG, "surfaceChanged");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (_discard){
                return;
            }
            LogUtil.d(TAG, "surfaceDestroyed");
            stopCamera();
            if (mEmptyFrameComposer != null){
                LogUtil.w(TAG, "mEmptyFrameComposer != null");
                return;
            }
            mEmptyFrameComposer = new EmptyFrameComposer();
            mEmptyFrameComposer.startEmptyFrameTrack();
        }
    }
}
