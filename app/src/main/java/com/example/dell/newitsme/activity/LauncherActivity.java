package com.example.dell.newitsme.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.SelfInfo;
import com.example.dell.newitsme.R;
import com.example.dell.newitsme.activity.base.ActivityEventBase;
import com.example.event.TurtleEvent;
import com.example.event.TurtleEventType;
import com.example.model.UserInfoModel;
import com.example.net.ApiListener;
import com.example.net.ClientApi;
import com.example.util.JsonHelper;
import com.example.util.SharedPreferencesUtil;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class LauncherActivity extends ActivityEventBase {
    private Activity mActivity;
    private static final String TAG = "LauncherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zidingyi_view_layout);
        mActivity = this;
        //这里是接收事件类的注册事件
        registerEventBus();//父类的方法
    }

    public void onEventMainThread(TurtleEvent event) {
        int type = event.getType();
        if(type == TurtleEventType.TYPE_API_DATA_OK ){
            /*数据回来了就去本地看看有没有个人信息，有就跳到主界面，没有就跳到登录界面*/
            //判断本地是否有个人信息，
           Object localUser =  SharedPreferencesUtil.getParam(this, SharedPreferencesUtil.KEY_SAVE_USERINFO,"");

            if(localUser.equals("")){
                //没有本地的数据，则跳到登录界面
                startActivity(new Intent(mActivity, LoginByEmailActivity.class));
                finish();
            }else {
                //如果有就解析本地保存的数据到内存，怎么保存的就怎么解
                if(localUser instanceof String){
                    /*
                    * {
                         "user":{"uid":3000957,"gender":0,"nick":"yabber0914","location":"","ulevel":2,"description":"","portrait":""},
                         "error_msg":"操作成功",
                         "dm_error":0
                      }
                    * */
                    String userString = (String)localUser;
                    JSONObject jsonObject = JsonHelper.jsonFromString(userString);
                    JSONObject user = jsonObject.optJSONObject("user");
                    /*  "user":{"uid":3000957,"gender":0,"nick":"yabber0914","location":"","ulevel":2,"description":"","portrait":""}, */
                    SelfInfo.inst()._userInfo.updateFromJson(user);
                 ;
                    ClientApi.info(SelfInfo.inst()._userInfo.uid,new ApiListener() { //解析本地数据到内存之后，检查token有没有过期
                        @Override
                        public void onResponse(JSONObject response) {
                            if (response == null) {
                                return;
                            }
                            
                            if (response.optInt("dm_error", -1) == 5) {//用户不存在了
                                Log.e(TAG, "ClientApi.info,user maybe deleted,goto relogin");
                                EventBus.getDefault().post(new TurtleEvent(TurtleEventType.TYPE_OVERDUE_TOKEN));//发送消息事件
                                return;
                            }

                            JSONObject userLateast = response.optJSONObject("user");//更新最新的数据，并发送事件，更新
                            if(userLateast != null){
                                SelfInfo.inst()._userInfo.updateFromJson(userLateast);
                                EventBus.getDefault().post(new TurtleEvent(TurtleEventType.TYPE_USER_INFO_UPDATE));//发送消息事件
                            }
                        }
                        @Override
                        public void onErrorResponse(int statusCode, String exceptionMessage) {
                              Log.i(TAG,"错误");
                        }
                    });

                    startActivity(new Intent(mActivity, MainActivity.class));
                    finish();
                }
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}