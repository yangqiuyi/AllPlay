package com.example.dell.newitsme.Animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import java.util.HashSet;
import java.util.Set;

public class AnimationUtils {

    public static final String TAG = "AnimationUtils";

    static final String ALPHA = "alpha";
    static final String PIVOT_X = "pivotX";
    static final String PIVOT_Y = "pivotY";
    static final String TRANSLATION_X = "translationX";
    static final String TRANSLATION_Y = "translationY";
    static final String ROTATION = "rotation";
    static final String ROTATION_X = "rotationX";
    static final String ROTATION_Y = "rotationY";
    static final String SCALE_X = "scaleX";
    static final String SCALE_Y = "scaleY";
    static final String SCROLL_X = "scrollX";
    static final String SCROLL_Y = "scrollY";
    static final String X = "x";
    static final String Y = "y";

    static final int DURATION_ROTATION_Y = 500;
    static final int DURATION_ROTATION_X = 500;

    public static final int DEFAULT_MSC = -1;


    public interface AnimaitonComplete
    {
        void onComplete(View v);
    }

    static public void showFromRight(final View view,int msec,AnimaitonComplete completer)
    {
       // LogUtil.d(TAG, "showFromRight");
        if (msec == -1){
            msec = 500;
        }
        doAnimation_X(view, msec, view.getHeight(), 0, View.VISIBLE, -1, completer);
    }

    static public void showFromLeft(final View view,int msec,AnimaitonComplete completer)
    {
     // LogUtil.d(TAG, "showFromLeft");
        if (msec == -1){
            msec = 500;
        }
        doAnimation_X(view, msec, -view.getWidth(), 0, View.VISIBLE, -1, completer);
    }

    static public void showFromBottom(final View view,int msec,AnimaitonComplete completer)
    {
       // LogUtil.d(TAG, "showFromBottom");
        if (msec == -1){
            msec = 500;
        }
        doAnimation_Y(view, msec, view.getHeight(), 0, View.VISIBLE, -1, completer);
    }

    static public void hideToLeft(final View view,int msec,AnimaitonComplete completer)
    {
       // LogUtil.d(TAG, "hideToLeft");
        if (msec == -1){
            msec = 500;
        }
        doAnimation_X(view, msec, 0, -view.getWidth(),-1, View.VISIBLE, completer);
    }

    static public void hideToRight(final View view,int msec,AnimaitonComplete completer)
    {
       // LogUtil.d(TAG,"hideToRight");
        if (msec == -1){
            msec = 500;
        }
        doAnimation_X(view, msec, 0, view.getHeight(), -1, View.INVISIBLE, completer);
    }

    static public void hideToBottom(final View view,int msec,AnimaitonComplete completer)
    {
       // LogUtil.d(TAG,"hideToBottom");
        if (msec == -1){
            msec = 500;
        }
        doAnimation_Y(view, msec, 0, view.getHeight(), -1, View.INVISIBLE, completer);
    }

    static public void showFromTop(final View view,int msec,AnimaitonComplete completer)
    {
      //  LogUtil.d(TAG, "showFromTop");
        if (msec == -1){
            msec = 500;
        }
        doAnimation_Y(view,msec, -view.getHeight(), 0, View.VISIBLE, -1,completer);
    }

    static public void hideToTop(final View view,int msec,AnimaitonComplete completer)
    {
      //  LogUtil.d(TAG,"hideToTop");
        if (msec == -1){
            msec = 500;
        }
        doAnimation_Y(view,msec, 0, -view.getHeight(), -1, View.INVISIBLE,completer);
    }

    static protected void doAnimation_Y(final View view,int msec,int
            fromOffsetY,int toOffsetY,final int visible_b,final int visiable_e,
                                        AnimaitonComplete completer_)
    {
        if (view == null){
            return;
        }
        if (view.getAnimation() != null){
            return ;
        }
        if (g_aniViews.contains(view)){
            return;
        }

        g_aniViews.add(view);

      //Log.d(TAG,"%x,doAnimation_Y",view.hashCode());

        final AnimaitonComplete completer = completer_;
        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.addListener(new AnimationListenerBase() {
            @Override
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                if (visible_b != -1) {
                    view.setVisibility(visible_b);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);

                g_aniViews.remove(view);

                if (visiable_e != -1) {
                    view.setVisibility(visiable_e);
                }
                if (completer != null) {
                    completer.onComplete(view);
                }
            }
        });




        animatorSet.playTogether(
                ObjectAnimator.ofFloat(view, TRANSLATION_Y, fromOffsetY, toOffsetY)
        );
        animatorSet.setDuration(DURATION_ROTATION_Y);
        animatorSet.start();
    }

    static boolean debugAnimation_X = true;
    static protected void doAnimation_X(final View view,int msec,
                                        int fromOffsetX,int toOffsetX,
                                        final int visible_b,final int visiable_e,
                                        AnimaitonComplete completer_)
    {
        if (view == null){
            if(debugAnimation_X){
               // LogUtil.d(TAG,"doAnimation_X view == null)");
            }
            return;
        }
        if (view.getAnimation() != null){
            if(debugAnimation_X){
               // LogUtil.d(TAG,"view.getAnimation() != null");
            }
            return ;
        }
        if (g_aniViews.contains(view)){
            if(debugAnimation_X){
              //  LogUtil.d(TAG,"g_aniViews.contains(view)");
            }
            return;
        }

        g_aniViews.add(view);
        if(debugAnimation_X){
           // LogUtil.d(TAG,"%x,doAnimation_X",view.hashCode());
        }

        final AnimaitonComplete completer = completer_;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimationListenerBase() {
            @Override
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                if(debugAnimation_X){
                   // LogUtil.d(TAG,"onAnimationStart,doAnimation_X");
                }

                if (visible_b != -1) {
                    view.setVisibility(visible_b);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                if(debugAnimation_X){
                //    LogUtil.d(TAG,"onAnimationEnd,doAnimation_X");
                }


                g_aniViews.remove(view);

                if (visiable_e != -1) {
                    view.setVisibility(visiable_e);
                }
                if (completer != null) {
                    completer.onComplete(view);
                }
            }
        });

        animatorSet.playTogether(
                ObjectAnimator.ofFloat(view, TRANSLATION_X, fromOffsetX, toOffsetX)
        );
        animatorSet.setDuration(DURATION_ROTATION_X);
        animatorSet.start();
    }

    public static void showViewRotateInfinite(View view){
        showViewRotateInfinite(view ,700);
    }

    public static void showViewRotateInfinite(View view , long duration){
        if(view == null || view.getAnimation() !=null){
            return;
        }
        RotateAnimation rotateAnimation = new RotateAnimation(0f ,360f ,RotateAnimation.RELATIVE_TO_SELF ,0.5f
                ,RotateAnimation.RELATIVE_TO_SELF ,0.5f);
        rotateAnimation.setDuration(duration);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        view.startAnimation(rotateAnimation);
    }

    public static void showViewAlphaInfinite(View view , long duration){
        if(view == null || view.getAnimation() !=null){
            return;
        }
        android.animation.ObjectAnimator objectAnimator = android.animation.ObjectAnimator.ofFloat(view ,"alpha" ,1f ,0 ,1f);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatMode(Animation.RESTART);
        objectAnimator.setRepeatCount(Animation.INFINITE);
        objectAnimator.start();
    }

    public static void showViewAlphaInfinite(View view , long duration, int repeatCount, android.animation.Animator.AnimatorListener animatorListener){
        if(view == null || view.getAnimation() !=null){
            return;
        }
        android.animation.ObjectAnimator objectAnimator = android.animation.ObjectAnimator.ofFloat(view ,"alpha" ,1f ,0 ,1f);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(repeatCount);
        objectAnimator.addListener(animatorListener);
        objectAnimator.start();
    }


    protected static Set<View> g_aniViews = new HashSet<>();
}
