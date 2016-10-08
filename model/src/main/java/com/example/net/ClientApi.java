package com.example.net;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.BuildConfig;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.example.SelfInfo;
import com.example.util.Devices;
import com.example.util.LocationHelper;
import com.example.util.encrypt.Encrypt;
import com.example.util.log.LogUtil;

import org.json.JSONObject;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClientApi {

    private static final String TAG = "ClientApi";

    private static final String REGISTER = "REGISTER";
    private static final String USER_LOGIN = "USER_LOGIN";
    private static final String USER_INFO = "USER_INFO";
    private static final String LIVE_USERS = "LIVE_USERS";
    private static final String LIVE_SIMPLEALL = "LIVE_SIMPLEALL";
    private static final String LIVE_HOMEPAGE = "LIVE_HOMEPAGE";

    private static final String LIVE_LATEST = "LIVE_LATEST";
    private static final String FOLLOW_RECORD = "FOLLOW_RECORD";

    private static final String PLATFORM = "platform";
    private static final String TOKEN = "access_token";
    private static final String EMAIL = "fb_mail";
    private static final String OPENID = "openid";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String NICK = "nick";


    public static void init() {
        ApiUrls.inst().init(); //new一个ApiUrls对象，调用里面的方法，从而从服务器更新列表
    }

    private static ClientApi _clientApi;

    public static ClientApi inst() {    //单例
        if (_clientApi == null) {
            _clientApi = new ClientApi();
        }
        return _clientApi;
    }

    /***
     * @param email
     * @param pass
     * @param nick
     * @return void
     */
    public void signUpByEmail(String email, String pass, String nick, ApiListener listener) {
        if (email == null || pass == null || nick == null) return;

        if (email == "" || pass == "" || nick == "") return;

        pass = Encrypt.SHA1(Encrypt.SHA1(pass) + "flybird");//加密

        HashMap param = new HashMap();

        param.put(PLATFORM, EMAIL);// private Map param = new HashMap();
        param.put(PASSWORD, pass);
        param.put(ACCOUNT, email);
        param.put(NICK, nick);
        callCmd_POST(REGISTER, param, listener);//key的值为REGISTER 根据key的值取链接" url": "http://54.169.250.64/user/account/register.php"
    }

    /*登录*/
    public void loginByEmail(String pass, String email, final ApiListener listener) {
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
    public static void info(int uid, final ApiListener listener) {
        Map params = new HashMap();
        params.put("id", uid);
        callCmd_GET(USER_INFO, params, listener);
    }

    private static String mapToKeyValue(Map params)   //?
    {
        StringBuilder builder = new StringBuilder();
        Iterator<String> keys = params.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = params.get(key).toString();
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(key + "=" + URLEncoder.encode(value));
        }
        return builder.toString();
    }

    private static void callCmd_POST(String key, Map param, final ApiListener listener) {
        String urlHead = ApiUrls.inst().keyToUrl(key);//根据 key的值来取url   key是application请求之后返回的
        if (urlHead == null || urlHead == "") {
            if (listener != null) {
                listener.onErrorResponse(-1, "no api found");//回调
            }
            return;
        }
        sendRequest(Request.Method.POST, urlHead, param, listener);//传进url，发送请求
    }

    /*补充url固定的一些元素*/
    private static String appendUrlParam(String url, String posfix) {
        if (TextUtils.isEmpty(posfix)) {
            return url;
        }
        StringBuilder urlBuilder = new StringBuilder(url);
        if (url.indexOf("?") > 0) {  //IndexOf()方法搜索在该字符串上是否出现了作为参数传递的字符串,如果找到字符串,则返回字符的起始位置 (0表示第一个字符,1表示第二个字符依此类推)如果说没有找到则返回 -1
            urlBuilder.append('&');  //所以这里的意思是 如果第二第三...次找到“？”就加“&”，如果没找到？，就加“？”
        } else {
            urlBuilder.append('?');
        }
        urlBuilder.append(posfix);
        return urlBuilder.toString();
    }

    /*首页热门列表*/
    public void hotList(final ApiListener listener) {
        Map params = new HashMap();
        callCmd_GET(LIVE_SIMPLEALL, params, new ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                if (listener != null) {
                    listener.onResponse(response);
                }
            }

            @Override
            public void onErrorResponse(int statusCode, String exceptionMessage) {
                if (listener != null) {
                    listener.onErrorResponse(statusCode, exceptionMessage);
                }
            }
        });
    }


    protected static void callCmd_GET(String cmd, Map param, final ApiListener listener) {
        String urlHead = ApiUrls.inst().keyToUrl(cmd);
        if (urlHead == null || urlHead == "") {
            if (listener != null) {
                listener.onErrorResponse(-1, "no api found");
            }
            return;
        }
        sendRequest(Request.Method.GET, urlHead, param, listener);
    }

    public static String getDefaultParam() {
        StringBuilder b = new StringBuilder();
        String token = SelfInfo.inst()._userInfo.token;

        if (!TextUtils.isEmpty(token)) {
            int uid = SelfInfo.uid();

            b.append("uid=" + uid);
            b.append("&sid=" + URLEncoder.encode(token));
        }
        if (b.length() != 0) {
            b.append("&");
        }

        //  String dev = getDeviceParam();
        //  b.append(dev);

        return b.toString();

    }

    private static void sendRequest(int method, String url, Map params, final ApiListener listener) {
        //get    Request请求方式
        if (method == Request.Method.GET) {//int GET = 0;
            if (params != null) {
                String posfix = mapToKeyValue(params);
                url = appendUrlParam(url, posfix);//用?& 把key_value键值存放在
            }
        }

        //默认参数
        url = appendUrlParam(url, getDefaultParam());

        //post get   Request请求方式
        final String fullUrl = url;
        Log.i(TAG, "fullUrl = " + fullUrl);

        JsonObjectRequest request = new JsonObjectRequest(method, fullUrl,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {//回调
                        if (listener != null) {
                            listener.onResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int errCode = -1;

                if (listener != null) {
                    if (error.networkResponse != null) {
                        errCode = error.networkResponse.statusCode;
                    }
                    listener.onErrorResponse(errCode, error.getMessage());
                }
            }
        });

        Queue.inst()._requestQueue.add(request);//volley框架请求http，把请求放到一个RequestQueue队列里面
    }


    /**
     * 获取最新页面的数据
     *
     * @param count
     * @param listener
     */
    public static void getNewList(int count, final ApiListener listener) {
        Map params = new HashMap();
        params.put("count", count);
        callCmd_GET(LIVE_LATEST, params, listener);
    }

    /*首页关注的直播列表*/
    public static void focusLivingList(final ApiListener listener) {
        Map params = new HashMap();
        callCmd_GET(LIVE_HOMEPAGE, params, listener);
    }

    /*首页回放列表*/
    public static void recorderList(int start, final ApiListener listener) {
        Map params = new HashMap();
        params.put("start", start);
        params.put("count", 50);
        callCmd_GET(FOLLOW_RECORD, params, listener);

    }

    /*拉取房间的用户列表*/
    public static void roomUsers(String id, int start, int count, final ApiListener listener) {
        Log.i(TAG, "roomUsers.id" + id);
        Map params = new HashMap();
        params.put("id", id);
        params.put("start", start);
        params.put("count", count);
        callCmd_GET(LIVE_USERS, params, listener);
    }


    public static String getDeviceParam() {
        StringBuilder b = new StringBuilder();
        b.append("ua=" + URLEncoder.encode(Devices.ua));


        b.append("&devi=" + URLEncoder.encode(Devices.devi));
        b.append("&imei=" + URLEncoder.encode(Devices.imei));
        b.append("&imsi=" + URLEncoder.encode(Devices.imsi));
        b.append("&conn=" + URLEncoder.encode(Devices.conn));
        b.append("&osversion=" + URLEncoder.encode(Devices.osversion));
        b.append("&lc=" + URLEncoder.encode(LocationHelper.inst().getLoaction()));
        b.append("&lang=" + URLEncoder.encode(LocationHelper.inst().getLang()));
        b.append("&ver=" + "" + BuildConfig.VERSION_CODE);

        return b.toString();
    }

    public interface VideoUrl {
        void urlIsReady(String urlPlay);
    }

    public static void getVideoUrl(String url, final VideoUrl listern) {

        /****
         *
         * {
         * "protocol":"cnc-hls",
         * "result":0,
         * "token":"",
         * "url":"http:\/\/th3.cdn.itsme.media\/m3u8\/cnc-hls\/2819822\/1475752551\/xevtuxijlx.m3u8"
         * }
         *
         * */
        final JsonRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int retCode = -1;

                try {
                    retCode = response.getInt("result");
                    if (retCode == 0) {
                        String urlInfo = response.optString("url");
                        listern.urlIsReady(urlInfo);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        Queue.inst()._requestQueue.add(request);// 请求
    }

}
