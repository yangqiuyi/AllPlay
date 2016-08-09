package com.example.dell.newitsme.event;


public class TurtleEvent {
    protected int eventType;
    protected Object param;

    public TurtleEvent(int type) {
        this.eventType = type;
    }

    public TurtleEvent(int type, Object param) {
        this.eventType = type;
        this.param = param;
    }

    public int getType(){
        return eventType;
    }

    public <T> T getParam(){
        return (T)param;
    }
}
