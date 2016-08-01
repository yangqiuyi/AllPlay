package com.example.dell.newitsme.net;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dell.newitsme.Consts;
import com.example.dell.newitsme.activity.FirstActivity;
import com.example.dell.newitsme.activity.MainActivity;
import com.example.dell.newitsme.util.JsonHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Random;


public class ApiUrls {
    
    public static final String TAG = "ApiUrls";

    public static final String KEY_API = "FlyApi";
    public static final String KEY_GATEWAY_IP = "FlyGatewayIP";
    public static final String KEY_ROOM_IP = "FlyRoomIP";
    public static final String KEY_API_MD5 = "KEY_API_MD5";

    public static ApiUrls g_inst ;   //单例

    public static ApiUrls inst(){
        if (g_inst == null){
            g_inst = new ApiUrls();
        }
        return g_inst;     //单例模式
    }

    public void init() {
        initApiMap();
    }

    public String keyToUrl(String key) {
        String url = null;
        if (_apis != null && !_apis.isEmpty()){
            url = _apis.get(key);
        }
        return url;
    }

    protected void initApiMap() {
        String md5 = "";
        updateApiMap(md5);
    }

    /*从服务器更新api列表*/
    private void updateApiMap(final String oldMd5) {
        String url = APP_API_URL + oldMd5;
        Log.d(TAG,"updateApiMap begin:" + url);
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"updateApiMap onResponse:" + response);
                //md5 省略逻辑
                loadApiMap(response);
                FirstActivity.get().sendEmptyMessage(0);
                //eventBUS
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "updateApiMap,onErrorResponse");
                //网络问题，需要重试
                updateApiMap("");
            }
        });

        Queue.inst()._requestQueue.add(request);
    }

    /*****
     *
     * @param rawApi
     * {
         "md5": "7c9d2b0c4a74854931686681ccbdf069",
         "dm_error": 0,
         "error_msg": "",
         "server": [
                 {
                     "key": "USER_RECOMMEND",
                     "type": 1001,
                     "url": "http://54.169.250.64/user/recommend.php"
                 },
                 {
                     "key": "USER_LOGIN",
                     "type": 1001,
                     "url": "http://54.169.250.64/user/account/login.php"
                 },
                。。。。。。
                。。。。。。
                。。。。。。
     *
     * @return true, parase successfully
     * */
    protected Boolean loadApiMap(String rawApi) {  //解json返回的代码  传进的参数就是返回的response
        Log.d(TAG,"loadApiMap");

        try {
            JSONObject apiObject = new JSONObject(rawApi);
            JSONArray apiArr =  apiObject.getJSONArray("server");//服务器返回的response的value用JSON解析  server相当于数组   看上面的解析
            int size = apiArr.length();
            for (int i = 0; i < size; i++) {
                JSONObject api = apiArr.getJSONObject(i);
                String key = api.getString("key");
                String url = api.getString("url");
                _apis.put(key, url);
            }
            return true;
        }catch (Exception e){
            Log.e(TAG,"loadApiMap Exception:" + e.toString());
        }

        return false;
    }

    private static final String APP_API_URL = "http://" + Consts.APP_API_HOST + "/serviceinfo/all.php?md5=";
    private  Dictionary<String ,String> _apis  = new Hashtable();;

    public static final String USER_LEVEL = "USER_LEVEL";
    public static final String USER_RECHARGE = "USER_RECHARGE";//充值
    public static final String USER_CASH = "USER_CASH";//提现
    public static final String ABOUT_RULES = "ABOUT_RULES";
    public static final String ABOUT_TERMS = "ABOUT_TERMS";
    public static final String ABOUT_POLICY = "ABOUT_POLICY";
    public static final String ABOUT_CONTACT = "ABOUT_CONTACT";
    public static final String FORGET_PASSWORD = "";//忘记密码
    private Dictionary<Integer,AddressInfo> g_roomAddress;
    private ArrayList<AddressInfo> g_gatewayAddress;
    private int _errCount = 0;
    public static final String TH_GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=th.media.itsme";
    public static final String GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=media.itsme";


}
