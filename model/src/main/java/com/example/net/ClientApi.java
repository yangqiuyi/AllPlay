package com.example.net;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.util.encrypt.Encrypt;

import org.json.JSONObject;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClientApi {

    private static final String TAG = "ClientApi";

    //private Map param = new HashMap();


    private static final String REGISTER = "REGISTER";
    private static final String GIFT_SEND = "GIFT_SEND";
    private static final String MSG_SEND = "MSG_SEND";
    private static final String FOLLOW = "FOLLOW";
    private static final String UNFOLLOW = "UNFOLLOW";

    private static final String LIVE_PRE = "LIVE_PRE";
    private static final String LIVE_START = "LIVE_START";
    private static final String LIVE_STOP = "LIVE_STOP";
    private static final String LIVE_KEEP = "LIVE_KEEPALIVE";
    private static final String USER_LOGIN = "USER_LOGIN";
    private static final String USER_INFO = "USER_INFO";
    private static final String STATISTIC_CONTRIBUTION = "STATISTIC_CONTRIBUTION";
    private static final String STATISTIC_INOUT = "STATISTIC_INOUT";
    private static final String NUM_RELATION = "NUM_RELATION";
    private static final String RELATION = "RELATION";
    private static final String LIVE_USERS = "LIVE_USERS";
    private static final String RECORDE_USER = "RECORDE_USER";
    private static final String GIFT_INFO = "GIFT_INFO";
    private static final String CONVERSION_RATE = "CONVERSION_RATE";
    private static final String USER_RECOMMEND = "USER_RECOMMEND";
    private static final String LIVE_GETTOP = "LIVE_GETTOP";
    private static final String LIVE_SIMPLEALL = "LIVE_SIMPLEALL";
    private static final String LIVE_HOMEPAGE = "LIVE_HOMEPAGE";
    private static final String LIVE_TICKER = "LIVE_TICKER";
    private static final String MSG_GET = "MSG_GET";
    private static final String UPDATE_PROTRAIT = "UPDATE_PROTRAIT";
    private static final String USER_UPDATE_PROFILE = "USER_UPDATE_PROFILE";
    private static final String RELATION_FOLLOWINGS = "RELATION_FOLLOWINGS";
    private static final String RELATION_FANS = "RELATION_FANS";
    private static final String LIVE_STATUS = "LIVE_STATUS";
    private static final String LIVE_STATISTIC = "LIVE_STATISTIC";
    private static final String USER_SEARCH = "USER_SEARCH";
    private static final String STATISTIC_INFO = "STATISTIC_INFO";
    private static final String ANONYMOUS = "ANONYMOUS";
    private static final String RELATION_BLACKS = "RELATION_BLACKS";
    private static final String RELATION_BLACK = "RELATION_BLACK";
    private static final String RELATION_BLACK_STAT = "RELATION_BLACK_STAT";
    private static final String RELATION_DEL_BLACK = "RELATION_DEL_BLACK";
    private static final String LIVE_IPS = "LIVE_IPS";
    private static final String NOTIFY_STATUS = "NOTIFY_STATUS";
    private static final String NOTIFY_SWITCH = "NOTIFY_SWITCH";
    private static final String NOTIFY_RECENT = "NOTIFY_RECENT";
    private static final String NOTIFY_BLACK = "NOTIFY_BLACK";
    private static final String NOTIFY_UNBLACK = "NOTIFY_UNBLACK";
    private static final String LIVE_LATEST = "LIVE_LATEST";
    private static final String FOLLOW_RECORD = "FOLLOW_RECORD";
    private static final String VIEW_RECORD = "VIEW_RECORD";
    private static final String VIEW_LIVE = "VIEW_LIVE";
    private static final String DEPOSIT = "DEPOSIT";
    private static final String TEST_LOG = "TEST_LOG";
    private static final String PRODUCT_INFO = "PRODUCT_INFO";
    private static final String RECORDS = "RECORDS";
    private static final String CHECK_VERSION = "CHECK_VERSION";
    private static final String WAKEUP = "WAKEUP";
    private static final String FEEDBACK = "FEEDBACK";
    private static final String USERLIVEINFO = "USERLIVEINFO";
    private static final String REPORT = "REPORT";
    private static final String UPDATE_LAST_INFO = "UPDATE_LAST_INFO";
    private static final String DELETE_RECORD = "DELETE_RECORD";
    private static final String FORGET_PASSWORD = "FORGET_PASSWORD";
    private static final String RESET_PASSWORD = "RESET_PASSWORD";
    private static final String DANMUKU_INFO = "DANMUKU_INFO";
    private static final String LIVE_BANNER_AD = "LIVE_BANNER_AD";//首页banner广告
    
    public static void init() {
        ApiUrls.inst().init(); //new一个ApiUrls对象，调用里面的方法，从而从服务器更新列表
    }

    private static ClientApi _clientApi;
    public static ClientApi inst(){    //单例
        if(_clientApi == null){
            _clientApi = new ClientApi();
        }
        return _clientApi;
    }
    
    /***
     * @param email
     * @param pass
     * @param nick
     * @return void 
     * */
    public void signUpByEmail(String email , String pass , String nick, ApiListener listener){
        if(email == null || pass == null || nick == null)return;

        if(email == "" || pass == "" || nick == "")return;

        pass = Encrypt.SHA1(Encrypt.SHA1(pass)+"flybird");//加密

        HashMap param = new HashMap();

        param.put(PLATFORM, EMAIL);// private Map param = new HashMap();
        param.put(PASSWORD, pass);
        param.put(ACCOUNT, email);
        param.put(NICK ,nick);
        callCmd_POST(REGISTER, param, listener);//key的值为REGISTER 根据key的值取链接" url": "http://54.169.250.64/user/account/register.php"
    }

    /*登录*/
    public  void loginByEmail(String pass, String email, final ApiListener listener) {
        HashMap param = new HashMap();

        pass = Encrypt.SHA1(Encrypt.SHA1(pass) + "flybird");//加密
        param.put(PLATFORM, EMAIL);
        param.put(TOKEN, pass);
        param.put(OPENID, email);

        callCmd_POST(USER_LOGIN, param, new ApiListener() {
            @Override
            public void onResponse(JSONObject response) {      //   回调处  ，回调处在callCmd_POST（）
                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(int statusCode, String exceptionMessage) {
                listener.onErrorResponse(statusCode, exceptionMessage);
          //      MixPanel.inst().track(MixPanel.Login_with_Token_failed, exceptionMessage == null ? "unknow" : exceptionMessage.toString());
            }
        });
    }

    /*获取用户的个人资料*/
    public static void info(int uid,final ApiListener listener)
    {
        Map params = new HashMap();
        params.put("id", uid);
        callCmd_GET(USER_INFO, params, listener);
    }

    private static String mapToKeyValue(Map params)   //?
    {
        StringBuilder builder = new StringBuilder();
        Iterator<String> keys =  params.keySet().iterator();
        while (keys.hasNext()){
            String key = keys.next();
            String value = params.get(key).toString();
            if (builder.length() > 0){
                builder.append("&");
            }
            builder.append(key + "=" + URLEncoder.encode(value));
        }
        return builder.toString();
    }

    private static void callCmd_POST(String key, Map param, final ApiListener listener)
    {
        String urlHead = ApiUrls.inst().keyToUrl(key);//根据 key的值来取url   key是application请求之后返回的
        if (urlHead == null || urlHead == ""){
            if (listener != null) {
                listener.onErrorResponse(-1, "no api found");//回调
            }
                return;
        }
        sendRequest(Request.Method.POST, urlHead, param, listener);//传进url，发送请求
    }

    /*补充url固定的一些元素*/
    private static String appendUrlParam( String url,String posfix)
    {
        if (TextUtils.isEmpty(posfix)){
            return url;
        }
        StringBuilder urlBuilder = new StringBuilder(url);
        if(url.indexOf("?") > 0){  //IndexOf()方法搜索在该字符串上是否出现了作为参数传递的字符串,如果找到字符串,则返回字符的起始位置 (0表示第一个字符,1表示第二个字符依此类推)如果说没有找到则返回 -1
            urlBuilder.append('&');  //所以这里的意思是 如果第二第三...次找到“？”就加“&”，如果没找到？，就加“？”
        }else {
            urlBuilder.append('?');
        }
        urlBuilder.append(posfix);
        return urlBuilder.toString();
    }

    /*首页热门列表*/
    public  void hotList(final ApiListener listener) {
        Map params = new HashMap();
        callCmd_GET(LIVE_SIMPLEALL, params, new ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                if (listener != null){
                    listener.onResponse(response);
                }
            }

            @Override
            public void onErrorResponse(int statusCode, String exceptionMessage) {
                if (listener != null){
                    listener.onErrorResponse(statusCode,exceptionMessage);
                }
            }
        });
    }


    protected static void callCmd_GET(String cmd, Map param, final ApiListener listener)
    {
        String urlHead = ApiUrls.inst().keyToUrl(cmd);
        if (urlHead == null || urlHead == ""){
            if (listener != null) {
                listener.onErrorResponse(-1, "no api found");
            }
            return;
        }
        sendRequest(Request.Method.GET, urlHead, param, listener);
    }





    private static void sendRequest(int method,String url,Map params,final ApiListener listener) {
        //get    Request请求方式
        if (method == Request.Method.GET) {//int GET = 0;
            if (params != null) {
                String posfix = mapToKeyValue(params);
                url = appendUrlParam(url, posfix);//用?& 把key_value键值存放在
            }
        }

        //post get   Request请求方式
        final String fullUrl = url;
        Log.i(TAG,"fullUrl = " +fullUrl);

        JsonObjectRequest request = new JsonObjectRequest(method, fullUrl,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {//回调
                        if (listener != null){
                            listener.onResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int errCode = -1;

                if (listener != null){
                    if (error.networkResponse != null){
                        errCode = error.networkResponse.statusCode;
                    }
                    listener.onErrorResponse(errCode, error.getMessage());
                }
            }
        });

        Queue.inst()._requestQueue.add(request);
    }

    private static final String PLATFORM = "platform";
    private static final String TOKEN = "access_token";
    private static final String SECRET = "secret";
    private static final String FACEBOOK = "facebook";
    private static final String TWITTER = "twitter";
    private static final String EMAIL = "fb_mail";
    private static final String OPENID = "openid";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String NICK = "nick";
    private static final String VK = "vk";

}
