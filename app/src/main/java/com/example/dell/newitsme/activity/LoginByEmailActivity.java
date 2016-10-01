package com.example.dell.newitsme.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dell.newitsme.R;
import org.json.JSONObject;

import java.util.PriorityQueue;

import com.example.net.ApiListener;
import com.example.SelfInfo;
import com.example.net.ClientApi;
import com.example.util.SharedPreferencesUtil;
import com.example.util.StrUtil;
import com.example.util.ToastUtil;

public class LoginByEmailActivity extends Activity {
    private static final String TAG = "LoginByEmailActivity";
    private Context context ;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private Button mButtonLogin;

    private String name;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        context = this;

        initUI();
    }

    private void  initUI(){
        context = this;
        TextView mtextview = (TextView)findViewById(R.id.tv_titlebar);
        mtextview.setText("邮箱登录");

        mEditTextEmail = (EditText)findViewById(R.id.et_login_em);
        mEditTextPassword = (EditText)findViewById(R.id.et_login_pw);
        mButtonLogin = (Button)findViewById(R.id.bt_login_by_email);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                   Log.i(TAG, "-- onClick --");
                   login();//要放在点击事件里面，如果放在onCreat里面，获得的是空1

           }
       });
    }

    private void login(){
        name = mEditTextEmail.getText().toString();
        password = mEditTextPassword.getText().toString();

        if(StrUtil.isEmpty(name)){
            ToastUtil.showToastLong(context,R.string.email_empty);
            return;
        }

        if(!StrUtil.isEmail(name)){
            ToastUtil.showToastLong(context,R.string.email_incorrect);
            return;
        }

        if(StrUtil.isEmpty(password)){
            ToastUtil.showToastLong(context,R.string.pass_empty);
            return;
        }

        // {"error_msg":"account not found","dm_error":404}
        //{"error_msg":"password error","dm_error":403}
        //{"uid":3000957,"session":"11G+5VWz+sKPsqHsgA3E5pE9WSmokZF5ajLn36oWjasEI=","error_msg":"操作成功","dm_error":0}
        ClientApi.inst().loginByEmail(password, name, new ApiListener() {
            @Override
            public void onResponse(final JSONObject response) {
                if(response == null)return;
                Log.i(TAG, "response = " + response.toString());

                int dm_error = -1;//赋初始值
                if (response != null) {
                    dm_error = response.optInt("dm_error", -1);
                    if (dm_error == 0) {//ok
                        final  String token = response.optString("session", "");
                        final int uid = response.optInt("uid");
                        //文件
                        SharedPreferencesUtil.setParam(context, SharedPreferencesUtil.KEY_TOKEN, token);
                        //内存
                        SelfInfo.inst()._userInfo.token = token;
                        SelfInfo.inst()._userInfo.uid = uid;

                        //获取个人信息
                        ClientApi.info(uid, new ApiListener(){
                            @Override
                            public void onResponse(JSONObject response){
                                int dm_error = -1;//赋初始值

                                if (response != null){
                                    Log.i(TAG,response.toString());

                                    dm_error = response.optInt("dm_error", -1);//这里需要解"dm_error"字段，如果为空，则默认是-1
                                    if (dm_error == 0){
                                        SelfInfo.inst()._userInfo.updateFromJson(response);//获得个人信息,在内存里面保存了个人信息

                                        //SharedPreferencesUtil   把返回的response 个人信息保存到本地  保存到的是json字段 String类型
                                        SharedPreferencesUtil.setParam(context,SharedPreferencesUtil.KEY_SAVE_USERINFO,response.toString());

                                        Intent intent = new Intent(context,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            }
                            @Override
                            public void onErrorResponse(int statusCode, String exceptionMessage) {
                                   Log.i(TAG,"错误");
                            }
                        });
                    }else {
                        switch (dm_error){
                            case 404:{
                                ToastUtil.showToastLong(context,getString(R.string.no_user));
                                break;
                            }
                            case 403:{
                                ToastUtil.showToastLong(context,getString(R.string.password_error));
                                break;
                            }
                            case -1:{
                                ToastUtil.showToastLong(context,getString(R.string.network_no_avaliable));
                                break;
                            }
                            default: {ToastUtil.showToastLong(context,getString(R.string.register_fail));
                                Log.i(TAG,"response = " + response.toString());
                            }
                        }
                    }
                }
            }

            @Override
            public void onErrorResponse(int statusCode, String exceptionMessage) {
                Log.e(TAG, " exceptionMessage = + exceptionMessage");
                ToastUtil.showToastLong(context, "onErrorResponse = " + exceptionMessage);
            }
        });
    }

}