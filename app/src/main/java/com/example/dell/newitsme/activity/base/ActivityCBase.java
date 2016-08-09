package com.example.dell.newitsme.activity.base;

import com.example.dell.newitsme.controller.ControllerBase;
import java.util.ArrayList;
import java.util.List;

//专门处理 界面 和 Controller
public abstract class ActivityCBase extends ActivityEventBase {

    protected List<ControllerBase> _controllers;

    public void addActivityController(ControllerBase child) {
        if (_controllers == null) {
            _controllers = new ArrayList<>();
        }
        _controllers.add(child);
    }

    @Override
    protected void onDestroy() {
        if (_controllers != null) {
            for (int i = 0; i < _controllers.size(); i++) {
                ControllerBase child = _controllers.get(i);
                child.internal_gc();
            }
            _controllers = null;
        }

        unregisterEventBus();
        super.onDestroy();
    }



}
