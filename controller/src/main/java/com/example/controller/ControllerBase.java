package com.example.controller;

import java.util.ArrayList;
import java.util.List;

public abstract class ControllerBase {
    private List<ControllerBase> _childController;

    //Activity 里面有 Controller
    public ControllerBase(ActivityCBase activity) {
        if(activity != null){
            activity.addActivityController(this);
        }
    }

    //Controller 里面有 Controller
    public ControllerBase(ControllerBase parent) {
        if(parent != null){
            parent.addChild(this);
        }
    }

    //Controller 里面有 Controller
    private void addChild(ControllerBase child) {
        if (_childController == null) {
            _childController = new ArrayList<>();
        }
        _childController.add(child);
    }

    //do not directly call it, it will call by internal_gc() when activity destroyed
    protected abstract void gc();

    //call by ActivityCBase when it destroyed
    public void internal_gc() {
        if (_childController != null) {
            for (int i = 0; i < _childController.size(); i++) {
                ControllerBase child = _childController.get(i);
                child.internal_gc();
            }
            _childController = null;
        }

        gc();
    }

}
