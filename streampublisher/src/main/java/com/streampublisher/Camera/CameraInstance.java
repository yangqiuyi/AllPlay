package com.streampublisher.Camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import java.io.IOException;
import java.util.List;

public class CameraInstance implements SurfaceHolder.Callback {

    public static final String TAG = "CameraInstance";
    public static final String CAMERA_ID_KEY = "camera_id";
    public static final String CAMERA_FLASH_KEY = "flash_mode";
    public static final String IMAGE_INFO = "image_info";

    private static final int PICTURE_SIZE_MAX_WIDTH = 1280;
    private static final int PREVIEW_SIZE_MAX_WIDTH = 640;

    private int mCameraID;
    private int mCameraFontID;
    private String mFlashMode;
    private Camera mCamera;
    private CameraPreview mPreviewView;
    private SurfaceHolder mSurfaceHolder;

    public CameraInstance() {
    }

    public void attach(Context context, CameraPreview preview) {
        mCameraID = getBackCameraID();
        PackageManager pm = context.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            mCameraFontID = CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCameraFontID = mCameraID;
        }

        //  mOrientationListener = new CameraOrientationListener(context);


        mFlashMode = Camera.Parameters.FLASH_MODE_AUTO;
        //mImageParameters = new ImageParameters();
        mPreviewView = preview;
        //mOrientationListener.enable();

        mPreviewView.getHolder().addCallback(this);
        setupCamera();

    }

    public void switchCamera() {
        if (mCameraID == CameraInfo.CAMERA_FACING_FRONT) {
            mCameraID = getBackCameraID();
        } else {
            mCameraID = getFrontCameraID();
        }
        restartPreview();
    }

    public void setupFlashMode(String flashMode) {

        if (flashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_AUTO)) {
            mFlashMode = Camera.Parameters.FLASH_MODE_ON;
        } else if (flashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_ON)) {
            mFlashMode = Camera.Parameters.FLASH_MODE_OFF;
        } else if (flashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_OFF)) {
            mFlashMode = Camera.Parameters.FLASH_MODE_AUTO;
        }

        setupCamera();

    }

    private void getCamera(int cameraID) {
        try {
            mCamera = Camera.open(cameraID);
            mPreviewView.setCamera(mCamera);
        } catch (Exception e) {
            Log.d(TAG, "Can't open camera with id " + cameraID);
            e.printStackTrace();
        }
    }

    /**
     * Restart the camera preview
     */
    private void restartPreview() {
        if (mCamera != null) {
            stopCameraPreview();
            mCamera.release();
            mCamera = null;
        }

        getCamera(mCameraID);
        startCameraPreview();
    }

    /**
     * Start the camera preview
     */
    private void startCameraPreview() {
        determineDisplayOrientation();
        setupCamera();

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();

            //setSafeToTakePhoto(true);
            setCameraFocusReady(true);
        } catch (IOException e) {
            Log.d(TAG, "Can't start camera preview due to IOException " + e);
            e.printStackTrace();
        }
    }

    /**
     * Stop the camera preview
     */
    private void stopCameraPreview() {
        //setSafeToTakePhoto(false);
        setCameraFocusReady(false);

        // Nulls out callbacks, stops face detection
        mCamera.stopPreview();
        mPreviewView.setCamera(null);
    }
//
//    private void setSafeToTakePhoto(final boolean isSafeToTakePhoto) {
//        mIsSafeToTakePhoto = isSafeToTakePhoto;
//    }

    private void setCameraFocusReady(final boolean isFocusReady) {
        if (this.mPreviewView != null) {
            mPreviewView.setIsFocusReady(isFocusReady);
        }
    }

    /**
     * Determine the current display orientation and rotate the camera preview
     * accordingly
     */
    private void determineDisplayOrientation() {
//        CameraInfo cameraInfo = new CameraInfo();
//        Camera.getCameraInfo(mCameraID, cameraInfo);
//
//        // Clockwise rotation needed to align the window display to the natural position
//        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
//        int degrees = 0;
//
//        switch (rotation) {
//            case Surface.ROTATION_0: {
//                degrees = 0;
//                break;
//            }
//            case Surface.ROTATION_90: {
//                degrees = 90;
//                break;
//            }
//            case Surface.ROTATION_180: {
//                degrees = 180;
//                break;
//            }
//            case Surface.ROTATION_270: {
//                degrees = 270;
//                break;
//            }
//        }
//
//        int displayOrientation;
//
//        // CameraInfo.Orientation is the angle relative to the natural position of the device
//        // in clockwise rotation (angle that is rotated clockwise from the natural position)
//        if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
//            // Orientation is angle of rotation when facing the camera for
//            // the camera image to match the natural orientation of the device
//            displayOrientation = (cameraInfo.orientation + degrees) % 360;
//            displayOrientation = (360 - displayOrientation) % 360;
//        } else {
//            displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
//        }
//
//       // mImageParameters.mDisplayOrientation = displayOrientation;
//       // mImageParameters.mLayoutOrientation = degrees;
//
//        mCamera.setDisplayOrientation(displayOrientation);
    }

    /**
     * Setup the camera parameters
     */
    private void setupCamera() {
        // Never keep a global parameters
        Camera.Parameters parameters = mCamera.getParameters();

        Size bestPreviewSize = determineBestPreviewSize(parameters);
        // Size bestPictureSize = determineBestPictureSize(parameters);

        parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
        // parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);

        // Set continuous picture focus, if it's supported
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }


        List<String> flashModes = parameters.getSupportedFlashModes();
        if (flashModes != null && flashModes.contains(mFlashMode)) {
            parameters.setFlashMode(mFlashMode);
        } else {
        }
        // Lock in the changes
        mCamera.setParameters(parameters);
    }

    private Size determineBestPreviewSize(Camera.Parameters parameters) {
        return determineBestSize(parameters.getSupportedPreviewSizes(), PREVIEW_SIZE_MAX_WIDTH);
    }

//    private Size determineBestPictureSize(Camera.Parameters parameters) {
//        return determineBestSize(parameters.getSupportedPictureSizes(), PICTURE_SIZE_MAX_WIDTH);
//    }

    private Size determineBestSize(List<Size> sizes, int widthThreshold) {
        Size bestSize = null;
        Size size;
        int numOfSizes = sizes.size();
        for (int i = 0; i < numOfSizes; i++) {
            size = sizes.get(i);
            boolean isDesireRatio = (size.width / 4) == (size.height / 3);
            boolean isBetterSize = (bestSize == null) || size.width > bestSize.width;

            if (isDesireRatio && isBetterSize) {
                bestSize = size;
            }
        }

        if (bestSize == null) {
            Log.d(TAG, "cannot find the best camera size");
            return sizes.get(sizes.size() - 1);
        }

        return bestSize;
    }

    private int getFrontCameraID() {
        return mCameraFontID;
    }

    //
    private int getBackCameraID() {
        return CameraInfo.CAMERA_FACING_BACK;
    }
//
//    r.onStop();
//    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;

        getCamera(mCameraID);
        startCameraPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // The surface is destroyed with the visibility of the SurfaceView is set to View.Invisible
        stopCameraPreview();
    }


}
