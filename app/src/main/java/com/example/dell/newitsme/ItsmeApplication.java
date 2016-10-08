package com.example.dell.newitsme;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.example.dell.newitsme.network.StreamHostRouter;
import com.example.dell.newitsme.network.StreamServerRouter;
import com.example.net.ClientApi;
import com.example.net.Queue;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.squareup.okhttp.OkHttpClient;
import java.util.HashSet;
import java.util.Set;


public class ItsmeApplication  extends Application {

    private static ItsmeApplication mApp;
    
    public static ItsmeApplication getInstance(){
        return mApp;
    }
    
    
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        initApp(getApplicationContext());
    }

    private void initApp(Context context){
        Queue.inst().init(context);//消息队列的单例
        ClientApi.init();
        frescoInit(context);
        StreamHostRouter.inst().init();


    }

    private void frescoInit(Context context)
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        Set<RequestListener> listeners = new HashSet<>();
        listeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory.newBuilder(context, okHttpClient)
                .setRequestListeners(listeners)
                .setBitmapsConfig(Bitmap.Config.ARGB_8888)
                .build();
        Fresco.initialize(context, config);
    }

}
