package com.example.dell.newitsme.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.dell.newitsme.R;
import com.example.dell.newitsme.event.TurtleEvent;
import com.example.dell.newitsme.event.TurtleEventType;

import de.greenrobot.event.EventBus;


public class FirstActivity extends Activity {

  //  private static FirstActivityHandler mHandle;
    private Activity mActivity;
    private static final String TAG = "FirstActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zidingyi_view_layout);
       // mHandle = new FirstActivityHandler();
        mActivity = this;
        //这里是接收事件类的注册事件
        EventBus.getDefault().register(this);
    }

    public void onEventMainThread(TurtleEvent event) {
        int type = event.getType();
        if(type == TurtleEventType.TYPE_API_DATA_OK ){
            startActivity(new Intent(mActivity, MainActivity.class));//数据回来了才跳转，要是数据还没回来就跳转则没有数据在MainActivity显示出来
            finish();
            Log.i(TAG, "TYPE_API_DATA_OK");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//当这个类销毁的时候解除注册
    }

   /* public static FirstActivityHandler get(){
        return mHandle;
    }

    public class FirstActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            startActivity(new Intent(mActivity, MainActivity.class));//数据回来了才跳转，要是数据还没回来就跳转则没有数据在MainActivity显示出来
            finish();
        }
    }*/

}