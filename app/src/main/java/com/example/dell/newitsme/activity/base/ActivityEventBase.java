package com.example.dell.newitsme.activity.base;

import com.example.controller.ActivityCBase;
import de.greenrobot.event.EventBus;

//专门处理事件相关
public abstract class ActivityEventBase extends ActivityCBase {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterEventBus();
    }
}
