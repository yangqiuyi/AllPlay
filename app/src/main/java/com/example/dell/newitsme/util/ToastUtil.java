package com.example.dell.newitsme.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.TimerTask;

//import media.itsme.common.R;


public class ToastUtil {
    
    private static Toast mToast;
    
    private static boolean checkToastNotNull(Context context) {
        if (mToast == null && context != null) {
            mToast = createCommonToast(context);
        }
        return (mToast != null);
    }

    public static void cancelToast(){
        if(mToast != null){
            mToast.cancel();
        }
        mToast = null;
    }
    
	public static Toast showToastShort(Context context, String msg) {
	    if (checkToastNotNull(context) && !isEmpty(msg) && context != null) {
            try{
                mToast.setText(msg);
                setToastIcon(null);
                mToast.setDuration(Toast.LENGTH_SHORT);
                mToast.show();
            }catch (Exception e){
                e.printStackTrace();
            }
	        return mToast;
	    }
	    return null;
	}

	public static Toast showToastShort(Context context, int resId) {
	    if (checkToastNotNull(context) && resId != 0 && context != null) {
            try{
                mToast.setText(resId);
                setToastIcon(null);
                mToast.setDuration(Toast.LENGTH_SHORT);
                mToast.show();
                return mToast;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
	}
	
	public static Toast showToastLong(Context context, String msg) {
        if (checkToastNotNull(context) && !isEmpty(msg) && context != null) {
            try{
                mToast.setText(msg);
                setToastIcon(null);
                mToast.setDuration(Toast.LENGTH_LONG);
                mToast.show();
            }catch (Exception e){
                e.printStackTrace();
            }
            return mToast;
        }
        return null;
	}

	public static Toast showToastLong(Context context, int resId) {
	    if (checkToastNotNull(context) && resId != 0 && context != null) {
            try{
                mToast.setText(resId);
                setToastIcon(null);
                mToast.setDuration(Toast.LENGTH_LONG);
                mToast.show();
            }catch (Exception e){
                e.printStackTrace();
            }
            return mToast;
        }
        return null;
	}

    public static Toast showToastLongWithGravity(Context context, String msg, int gravity, int yOffset) {
        if (!isEmpty(msg) && context != null) {
            Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
            toast.setText(msg);
            try{
                toast.setGravity(gravity, 0, yOffset);
                checkToastNotNull(context);
                setToastIcon(null);
                toast.show();
            }catch (Exception e){
                e.printStackTrace();
            }
            return toast;
        }
        return null;
    }

    /**
     * 显示带icon的Toast
     * @param context
     * @param iconResId 图片id
     * @param msg 文字内容
     * @param duration 显示时长 Toast.LENGTH_LONG or Toast.LENGTH_SHORT
     * @return
     */
    public static Toast showToastWithIcon(Context context, int iconResId, String msg, int duration) {
        return showToastWithIcon(context, context.getResources().getDrawable(iconResId), msg, duration);
    }

    public static Toast showToastWithIcon(Context context, Drawable iconDrawable, String msg, int duration) {
        if (checkToastNotNull(context)  && context != null) {
            try{
                mToast.setText(msg);
                mToast.setDuration(duration);
                setToastIcon(iconDrawable);
                mToast.show();
            }catch(Exception e){
                e.printStackTrace();
            }
            return mToast;
        }
        return null;
    }

    private static void setToastIcon(Drawable iconDrawable) {
        TextView messageText = (TextView) mToast.getView().findViewById(android.R.id.message);
        if (iconDrawable != null && messageText != null) {
            iconDrawable.setBounds(0, 0, iconDrawable.getMinimumWidth(), iconDrawable.getMinimumHeight());
            messageText.setCompoundDrawables(iconDrawable, null, null, null);
        }
    }

    public static Toast showCustomToast(Context context, int resId, int iconId) {
        if (resId != 0 && context != null) {
            //因为有图片，需要新建一个Toast，不然会影响mToast显示
            Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
            toast.setText(resId);
            toast.setGravity(Gravity.CENTER, 0, 0);
            LinearLayout toastView = (LinearLayout) toast.getView();
            ImageView image = new ImageView(context);
            image.setImageResource(iconId);
            toastView.addView(image, 0);
            toast.show();
            return toast;
        }
        return null;
    }

    public static Toast createCommonToast(Context context) {
        if (context != null) {
            Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            View ParentView = toast.getView();
         //   ParentView.setBackgroundResource(R.color.transparent_color);
            TextView messageText = (TextView) ParentView.findViewById(android.R.id.message);
            messageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            messageText.setTextColor(Color.WHITE);
            messageText.setGravity(Gravity.CENTER);
            GradientDrawable bgDrawable = new GradientDrawable();
            bgDrawable.setColor(Color.parseColor("#CC000000"));
       //     bgDrawable.setCornerRadius(DensityUtil.dip2px(context, 3));
            messageText.setBackgroundDrawable(bgDrawable);
          //  messageText.setPadding(DensityUtil.dip2px(context, 18), DensityUtil.dip2px(context, 8), DensityUtil.dip2px(context, 18), DensityUtil.dip2px(context, 8));
       //     messageText.setCompoundDrawablePadding(DensityUtil.dip2px(context, 7));
            return toast;
        }
        return null;
    }

    public static boolean isEmpty(String str) {
	    if(str != null) {
	        str = str.trim();
	        return TextUtils.isEmpty(str);
	    }
	    return true;
	}

    public static Toast createToast(Context context){
        Toast toast = new Toast(context);
        return toast;
    }

    public static class RemindTask extends TimerTask {
        Toast toast = null;
        int showTime = 3000;
        boolean isCancel = false;

        public RemindTask(Toast toast) {
            this.toast = toast;
        }
        public RemindTask(Toast toast,int showTime) {
            this.toast = toast;
            this.showTime = showTime;
        }

        @Override
        public boolean cancel(){
            super.cancel();
            isCancel = true;
            return true;
        }

        @Override
        public void run() {
            //获取当前时间
            long firstTime = System.currentTimeMillis();
            while(System.currentTimeMillis()-firstTime < showTime){//显示十秒
                if(toast != null){
                    if(isCancel){
                        toast.cancel();
                        break;
                    }else {
                        toast.show();
                    }
                }else {
                    break;
                }
            }
        }
    }
}
