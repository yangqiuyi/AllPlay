package com.streampublisher;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;




/**
 *
 */
public class VideoSurfaceView extends SurfaceView{

    public static final String TAG = "VideoSurfaceView";

    private static final double ASPECT_RATIO = 480.0 / 640.0;

    public VideoSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public VideoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

    }


    /**
     * Measure the view and its content to determine the measured width and the
     * measured height
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        final boolean isPortrait =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        int heightNew = height;
        int widthNew = width;
        if (width < height) {
            widthNew = (int) (height * ASPECT_RATIO + 0.5);
        } else {
            heightNew = (int) (width / ASPECT_RATIO + 0.5);
        }

        setMeasuredDimension(widthNew, heightNew);

        //Log.d(TAG,"onMeasure,[%d-%d]->[%d,%d]",width,height,widthNew,heightNew);
    }

}
