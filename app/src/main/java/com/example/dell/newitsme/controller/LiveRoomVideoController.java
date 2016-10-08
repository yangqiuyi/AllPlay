package com.example.dell.newitsme.controller;


import android.net.Uri;
import android.util.Log;
import android.view.SurfaceView;
import com.example.dell.newitsme.ItsmeApplication;
import com.example.dell.newitsme.network.StreamHostRouter;
import com.example.dell.newitsme.player.ExoPlayerWrap;
import com.example.net.ClientApi;
import com.google.android.exoplayer.util.Util;
public class LiveRoomVideoController extends ControllerBase{


    private  StreamHostRouter mStreamHostRouter ;
    private static final String TAG = "LiveRoomVideoController";
    private SurfaceView mSurfaceView;
    private String mPlayUrl = "";
    private ExoPlayerWrap mPlayer;

    public LiveRoomVideoController(ActivityCBase activity) {
        super(activity);
    }

    /***
     * http://q.stream.itsme.media:9999/index.php
     * ?
     * name=PlayStream
     * &player=3000957
     * &token=11G%2B5VWz%2BsKPsqHsgA3E5pE2ySP7G9kSeY2ZYgOHPW8%2B0%3D
     * &streamname=4341983&sessionid=7573709306045502&quality=0
     * &param=ua%3D%26devi%3D%26imei%3D%26imsi%3D%26conn%3D%26osversion%3D%26lc%3D%26lang%3D%26ver%3D1
     * &protocol=
     *
     *
     * {
         protocol: "cnc-hls",
         result: 0,
         token: "",
         url: "http://th3.cdn.itsme.media/m3u8/cnc-hls/4341983/1475737185/kffrmvggnc.m3u8"
     }
     *
     * **/
    public  void  init(String SteamName, String session_id, SurfaceView surfaceView){
        if(SteamName == null || session_id == null || surfaceView == null)return;;

        mStreamHostRouter = StreamHostRouter.inst();
        final String url = mStreamHostRouter.formQueryUrl(false,SteamName,session_id);
        Log.i(TAG,url);

        mSurfaceView = surfaceView;

        ClientApi.getVideoUrl(url, new ClientApi.VideoUrl() {//获得直播url
            @Override
            public void urlIsReady(String urlPlay) {
                mPlayUrl = urlPlay;
                Log.d(TAG, "url is ok = " + mPlayUrl);

                startWatch();
            }
        });

    }



    public void startWatch(){
        if(mPlayUrl == null || mPlayUrl.equals(""))return;

        //.m3u8  hls 协议
        if(mPlayer == null){
            mPlayer = new ExoPlayerWrap(Util.TYPE_HLS, ItsmeApplication.getInstance().getApplicationContext());
        }

        Uri uri = Uri.parse(mPlayUrl);
        mPlayer.setSurface(mSurfaceView.getHolder().getSurface());
        mPlayer.play(uri);
    }

    @Override
    protected void gc() {

    }
}
