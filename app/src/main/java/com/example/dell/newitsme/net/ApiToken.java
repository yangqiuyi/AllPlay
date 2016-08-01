package com.example.dell.newitsme.net;


public class ApiToken
{
    public static final String TAG = "ApiToken";

    public static ApiToken g_inst ;
    public static ApiToken inst(){
        if (g_inst == null){
            g_inst = new ApiToken();
        }
        return g_inst;
    }

 /*   public static int uid(){
       return selfInfo().id;
    }

    public static UserInfoModel selfInfo(){
        return ApiToken.inst()._userInfo;
    }*/

    private static boolean _isNewUser = false;
    public static boolean isNewUser(){
        return _isNewUser;
    }

    public static String ip(){
        return ApiToken.inst()._ip;
    }

    public static String token(){
        return ApiToken.inst()._token;
    }

    public Boolean load()
    {
        return false;
        /*
        SharedPreferences sp = TurtleApplication.inst().getSharedPreferences(SELF_INFO_PRS, Context.MODE_PRIVATE);
        String token = sp.getString(SELF_INFO_TOKEN, null);
        int uid = sp.getInt(SELF_INFO_UID, -1);
        if (token != null && uid > 0){
            _userInfo.id = uid;
            _token = token;

            Log.d(TAG,"found local token:%s", _token);
            Log.d(TAG, "found local userId:" + _userInfo.id);
            String json = sp.getString(SELF_INFO_JSON, null);
            if (json != null){
                Log.d(TAG,"found local userinfo:%s\n", json);
                UserInfoModel model = JsonHelper.objectFromJsonString(json,UserInfoModel.class);
                if (model != null){
                    _userInfo = model;
                    _lastLevel = model.ulevel;
                    CrashUtils.setUserId(_userInfo.id);
                    CrashUtils.setUserName(_userInfo.nick);
                }
            }
            _userLoginType = sp.getString(SELF_LOGIN_TYPE, UNKNOW);
            return true;
        }
        return false;*/
    }

    public void save(int uid,String token)
    {
      /*  _userInfo.id = uid;
        _token = token;
        SharedPreferences sp = TurtleApplication.inst().getSharedPreferences(SELF_INFO_PRS, Context.MODE_PRIVATE);
        if (sp != null){
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(SELF_INFO_UID, uid);
            editor.putString(SELF_INFO_TOKEN, token);
            editor.commit();
        }*/
    }

    public void save()
    {
      /*  SharedPreferences sp = TurtleApplication.inst().getSharedPreferences(SELF_INFO_PRS, Context.MODE_PRIVATE);
        if (sp != null){
            SharedPreferences.Editor editor = sp.edit();

            UserInfoModel userInfo = selfInfo();
            editor.putString(SELF_INFO_JSON, JsonHelper.toString(userInfo));
            editor.commit();

            int level = selfInfo().ulevel;
            if ( _lastLevel >= 1 && _lastLevel < level ){
                AppFlyerMgr.inst().level(level);
            }
            _lastLevel = level;
        }*/
    }

    private boolean _startFromManifestLaunch = false;
    public void markStartFromManifestLaunch(){
        _startFromManifestLaunch = true;
    }
    public boolean isStartFromManifestLaunch(){
        return _startFromManifestLaunch;
    }

    public static final String MAIL = "email";
    public static final String FACEBOOK = "facebook";
    public static final String TWITTER = "twitter";
    public static final String UNKNOW = "unknow";
    public static final String VK = "vk";


    public void setLoginType(String type)
    {
    /*    _userLoginType = type;
        SharedPreferences sp = TurtleApplication.inst().getSharedPreferences(SELF_INFO_PRS, Context.MODE_PRIVATE);
        if (sp != null){
            SharedPreferences.Editor editor = sp.edit();

            editor.putString(SELF_LOGIN_TYPE, type);
            editor.commit();
        }*/
    }

    public void setIP(String ip){
        _ip = ip;
    }

   /* public void updateInout(InoutModel.Inout inout){
        _inout = inout;
    }*/

    public void updateMoney(int gold){
        _moneyCount = gold;
    }
    /*还有多少钱*/
    public int getMoneyCount(){return _moneyCount;}

    /*收入与送出*/
  /*  public InoutModel.Inout getInout(){
        return _inout;
    }*/

    public String getLoginType(){
        return _userLoginType;
    }

    public void clear()
    {
       /* _userInfo = new UserInfoModel();
        _token = "";
        SharedPreferences sp = TurtleApplication.inst().getSharedPreferences(SELF_INFO_PRS, Context.MODE_PRIVATE);
        if (sp != null){
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(SELF_INFO_JSON);
            editor.remove(SELF_INFO_UID);
            editor.remove(SELF_INFO_TOKEN);
            editor.commit();
        }*/
    }

    private String _token = "";
    private int _moneyCount = 0; //我的余额

    private String _ip = "";
  // private InoutModel.Inout _inout = new InoutModel.Inout();
  //  private UserInfoModel _userInfo = new UserInfoModel();
    private String _userLoginType = ApiToken.UNKNOW;
    public String account = null;//提现账号


    private int _lastLevel = -1;

    private static String SELF_INFO_PRS = "SELF_INFO_PRS";
    private static String SELF_INFO_UID = "SELF_INFO_UID";
    private static String SELF_INFO_TOKEN = "SELF_INFO_TOKEN";
    private static String SELF_INFO_JSON = "SELF_INFO_JSON";
    private static String SELF_LOGIN_TYPE = "SELF_LOGIN_TYPE";


}
