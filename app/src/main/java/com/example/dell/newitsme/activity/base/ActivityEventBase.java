package com.example.dell.newitsme.activity.base;

import android.widget.Toast;

import com.example.dell.newitsme.controller.ActivityCBase;
import com.example.event.TurtleEvent;
import com.example.event.TurtleEventType;

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

    public  void onEventMainThread(TurtleEvent event){
        int type = event.getType();
        if (type == TurtleEventType.TYPE_OVERDUE_TOKEN) {
              Toast.makeText(this,"Token 过期",Toast.LENGTH_LONG);
        }
    }

}
