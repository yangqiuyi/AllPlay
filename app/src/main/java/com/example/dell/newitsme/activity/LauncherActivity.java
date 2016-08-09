package com.example.dell.newitsme.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.example.dell.newitsme.R;
import com.example.dell.newitsme.activity.base.ActivityCBase;
import com.example.dell.newitsme.event.TurtleEvent;
import com.example.dell.newitsme.event.TurtleEventType;

public class LauncherActivity extends ActivityCBase {
    private Activity mActivity;
    private static final String TAG = "LauncherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zidingyi_view_layout);
        mActivity = this;
        //这里是接收事件类的注册事件
        registerEventBus();//父类的方法
    }

    public void onEventMainThread(TurtleEvent event) {
        int type = event.getType();
        if(type == TurtleEventType.TYPE_API_DATA_OK ){
            startActivity(new Intent(mActivity, MainActivity.class));
            //数据回来了才跳转，要是数据还没回来就跳转则没有数据在MainActivity显示出来
            finish();
            Log.i(TAG, "TYPE_API_DATA_OK");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}