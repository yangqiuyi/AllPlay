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
import com.example.dell.newitsme.net.ApiListener;
import com.example.dell.newitsme.net.ApiToken;
import com.example.dell.newitsme.net.ClientApi;
import com.example.dell.newitsme.util.Encrypt;
import com.example.dell.newitsme.util.StrUtil;
import com.example.dell.newitsme.util.ToastUtil;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;


public class LoginByEmailActivity extends Activity {
    private static final String TAG = "LoginByEmailActivity";
    Context context ;
    EditText mEditTextEmail;
    EditText mEditTextPassword;
    Button mButtonLogin;

    String name;
    String password;
    PriorityQueue priorityQueue;
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
            public void onResponse(JSONObject response) {
                if(response == null)return;

                Log.i(TAG, "response = " + response.toString());
                ToastUtil.showToastLong(context,"onResponse = " + response.toString());

                int dm_error = response.optInt("dm_error", -1);//根据JSONO返回的关键字dm_error，给dm_error赋值
                switch (dm_error){
                    case 0:{ //ok
                        Intent intent = new Intent();
                        intent.setClass(context, MainActivity.class);
                        startActivity(intent);
                        break;
                    }
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

            @Override
            public void onErrorResponse(int statusCode, String exceptionMessage) {
                Log.e(TAG, " exceptionMessage = + exceptionMessage");
                ToastUtil.showToastLong(context, "onErrorResponse = " + exceptionMessage);
            }
        });
    }

}