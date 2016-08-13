package com.example.net;

import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.Consts;
import com.example.event.TurtleEvent;
import com.example.event.TurtleEventType;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Dictionary;
import java.util.Hashtable;

import de.greenrobot.event.EventBus;


public class ApiUrls {
    
    public static final String TAG = "ApiUrls";
    private static final String APP_API_URL = "http://" + Consts.APP_API_HOST + "/serviceinfo/all.php?md5=";
    private  Dictionary<String ,String> _apis  = new Hashtable();;
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
               // LauncherActivity.get().sendEmptyMessage(0);换成eventBUS
                //eventBUS
                EventBus.getDefault().post(new TurtleEvent(TurtleEventType.TYPE_API_DATA_OK));//发送消息事件
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

}
