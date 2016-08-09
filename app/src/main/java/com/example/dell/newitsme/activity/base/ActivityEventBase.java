package com.example.dell.newitsme.activity.base;

import android.support.v7.app.AppCompatActivity;

import de.greenrobot.event.EventBus;

//专门处理事件相关
public abstract class ActivityEventBase extends AppCompatActivity {
    protected Boolean isRegisterEventBus = false;

    protected void registerEventBus() {
        if(!isRegisterEventBus){
            isRegisterEventBus = true;
            EventBus.getDefault().register(this);
        }
    }

    protected void unregisterEventBus() {
        if (isRegisterEventBus){
            EventBus.getDefault().unregister(this);
            isRegisterEventBus = false;
        }
    }
}
