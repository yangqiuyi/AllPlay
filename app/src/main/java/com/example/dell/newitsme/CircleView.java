package com.example.dell.newitsme;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CircleView extends View {

    private Paint mpaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    Collection  collection;
    List list;
    Collections collections;

    public CircleView(Context context, AttributeSet attrs, int mColor) {
        super(context, attrs);
        mpaint.setColor(mColor);
    }

    @Override
    protected  void onDraw(Canvas canvas){
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        int radius = Math.min(width,height) / 2;

        canvas.drawCircle(width / 2,height / 2,radius,mpaint);

    }
}
