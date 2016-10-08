package com.example.util;


public class LocationHelper {
    public static final String TAG = "LocationHelper";
    private String mLocation = "";
    private boolean mLocating = false;
    private String language = "";
    private static LocationHelper mInstance;

    public static LocationHelper inst() {
        if (mInstance == null){
            mInstance = new LocationHelper();
        }
        return mInstance;
    }

    public String getLoaction(){
        return mLocation;
    }

    public String getLang(){
        return language;
    }

}
