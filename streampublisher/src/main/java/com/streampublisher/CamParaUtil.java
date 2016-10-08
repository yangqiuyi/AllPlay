package com.streampublisher;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Surface;


public class CamParaUtil {
    private static final String TAG = "CamParaUtil";
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private static CamParaUtil myCamPara = null;

    private CamParaUtil() {

    }

    public static CamParaUtil inst() {
        if (myCamPara == null) {
            myCamPara = new CamParaUtil();
            return myCamPara;
        } else {
            return myCamPara;
        }
    }

    public static Size  getPropPreviewSize(Camera camera,int width,int height) {

        List<Camera.Size> list = camera.getParameters().getSupportedPreviewSizes();
        Collections.sort(list, new CameraSizeComparator());

        float th = (float)width/height;
        int i = 0;
        for (Size s : list) {
            int off = Math.abs(s.width - width);
            if ((off < 10 ) && equalRate(s, th)) {
                Log.d(TAG, "PreviewSize:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;//如果没找到，就选最小的size
        }
        return list.get(i);
    }

    public Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for (Size s : list) {
            if ((s.width >= minWidth) && equalRate(s, th)) {
                Log.i(TAG, "PictureSize : w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;//如果没找到，就选最小的size
        }
        return list.get(i);
    }

    public static boolean equalRate(Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }

    public static class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Size lhs, Size rhs) {
            // TODO Auto-generated method stub
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }

    }

    public static void printSupportPreviewSize(Camera camera) {
        Camera.Parameters params = camera.getParameters();
        List<Size> previewSizes = params.getSupportedPreviewSizes();
        for (int i = 0; i < previewSizes.size(); i++) {
            Size size = previewSizes.get(i);
            Log.d(TAG, "previewSizes:width = " + size.width + " height = " + size.height);
        }
    }


    public static void printSupportPictureSize(Camera.Parameters params) {
        List<Size> pictureSizes = params.getSupportedPictureSizes();
        for (int i = 0; i < pictureSizes.size(); i++) {
            Size size = pictureSizes.get(i);
            Log.d(TAG, "pictureSizes:width = " + size.width
                    + " height = " + size.height);
        }
    }

    public static void printSupportFocusMode(Camera camera) {
        Camera.Parameters params = camera.getParameters();
        List<String> focusModes = params.getSupportedFocusModes();
        for (String mode : focusModes) {
            Log.d(TAG, "focusModes--" + mode);
        }
    }

    public static int determineDisplayOrientation(int cameraId, int camera_orientation,int activity_rotation) {

        int degrees = 0;
        switch (activity_rotation) {
            case Surface.ROTATION_0: {
                degrees = 0;
                break;
            }
            case Surface.ROTATION_90: {
                degrees = 90;
                break;
            }
            case Surface.ROTATION_180: {
                degrees = 180;
                break;
            }
            case Surface.ROTATION_270: {
                degrees = 270;
                break;
            }
        }

        int displayOrientation;

        if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayOrientation = (camera_orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else {
            displayOrientation = (camera_orientation - degrees + 360) % 360;
        }


        return displayOrientation;
    }
}