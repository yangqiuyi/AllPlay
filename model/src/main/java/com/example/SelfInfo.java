package com.example;

import com.example.model.UserInfoModel;

/*保存个人信息  public UserInfoModel _userInfo = new UserInfoModel();*/
public class SelfInfo {
    public static final String TAG = "SelfInfo";
    public  UserInfoModel _userInfo = new UserInfoModel();
    public static SelfInfo g_inst ;

    public synchronized static SelfInfo inst(){
        if (g_inst == null){
            g_inst = new SelfInfo();
        }
        return g_inst;
    }


}
