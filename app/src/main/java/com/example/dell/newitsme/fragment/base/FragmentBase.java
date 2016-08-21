package com.example.dell.newitsme.fragment.base;

import android.support.v4.app.Fragment;
import de.greenrobot.event.EventBus;

public class FragmentBase extends Fragment{

    protected Boolean _isRegisterEventBus = false;

    protected void registerEventBus() {
        if(!_isRegisterEventBus){//_isRegisterEventBus的作用：无论调多少次这个方法，只会注册一次事件
            _isRegisterEventBus = true;
            EventBus.getDefault().register(this);
        }
    }

    protected void unregisterEventBus() {
        if (_isRegisterEventBus){
            EventBus.getDefault().unregister(this);
            _isRegisterEventBus = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterEventBus();
    }

}
