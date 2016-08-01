package com.example.dell.newitsme;

import android.app.Application;

/**
 * 存放常量
 */
public class Consts extends Application {

    public static final String APP_API_HOST = "api.itsme.media";//"api.flybird.tv";//"api." + APP_HOST;  //域名

    public static  int TotalRequestCount=5;//限定请求的次数
    public static String APP_HOST = "itsme.media";
    public static final String APP_API_HOST_TEST = "test.api.ushow.media";
    public static final String DEFAULT_STREAM_HOST = "q.stream.itsme.media";//"q.stream." + APP_API_HOST;
    public static final int STREAM_HOST_PORT = 9999;
    public static String STREAM_HOST_URL_PATH = "/index.php";
    public static final int NOTIFY_ID = 0;

    public static  int CYCLE_TIME = 30 * 1000;//定时任务（热门/最新）
    public static  int TIME_HOT_LIST = 60 * 1000;//请求热门/最新全部数据的时间间隔

    public static final int NUM_VIEWERS = 10000;//数量比较少的时候，传大大，拉所有
    public static final int NUM_RE_FREASH = 10;//数量比较大时候，大于 NUM_CONDITION，固定拉取人数
    public static final int NUM_CONDITION = 50;//临界值

    public static final long TIME_CONDITION_EVENT_FOLLOWERS_UPDATA = 2 * 1000;//fan关系列表，狂点，有效时间条件
    public static final int PERMISSION_NEED_CAMERA = 1;//需要摄像头权限
    public static final int PERMISSION_NEED_AUDIO = 2;//需要录音权限



}
