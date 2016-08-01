package com.example.dell.newitsme.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.example.dell.newitsme.R;


public class FirstActivity extends Activity {

    private static FirstActivityHandler mHandle;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zidingyi_view_layout);
        mHandle = new FirstActivityHandler();
        mActivity = this;
    }

    public static FirstActivityHandler get(){
        return mHandle;
    }

    public class FirstActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            startActivity(new Intent(mActivity, MainActivity.class));//数据回来了才跳转，要是数据还没回来就跳转则没有数据在MainActivity显示出来
            finish();
        }
    }

}