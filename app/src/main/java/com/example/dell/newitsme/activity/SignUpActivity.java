package com.example.dell.newitsme.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.example.dell.newitsme.R;
import com.example.net.ApiListener;
import com.example.net.ClientApi;
import com.example.util.Animation.AnimationUtils;
import com.example.util.StrUtil;
import com.example.util.ToastUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;

public class SignUpActivity extends Activity {

    private static final String TAG = "SignUpActivity";
    public static   String email;
    public static  String password;

     private EditText editText_name;
     private EditText editText_email;
     private EditText editText_password;
     private Context context ;
     private HashSet hashSet;
    private HashMap hashMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        context = this;
        //
        editText_name  = (EditText)findViewById(R.id.et_name);
        editText_email = (EditText)findViewById(R.id.et_email);
        editText_password = (EditText)findViewById(R.id.et_password);

       findViewById(R.id.bt_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
                flingUp();
            }
        });


    }



    private void signup(){
        String name = editText_name.getText().toString();
        String email = editText_email.getText().toString();
        String pwd = editText_password.getText().toString();

        if(StrUtil.isEmpty(name)){
            ToastUtil.showToastLong(context,R.string.nick_empty);
            return;
        }

        if(StrUtil.isEmpty(email)){
            ToastUtil.showToastLong(context,R.string.email_empty);
            return;
        }

        if(!StrUtil.isEmail(email)){
            ToastUtil.showToastLong(context,R.string.email_incorrect);
            return;
        }

        if(StrUtil.isEmpty(pwd)){
            ToastUtil.showToastLong(context,R.string.pass_empty);
            return;
        }

        if (pwd.length()<6){
            ToastUtil.showToastLong(context,R.string.pass_length_incorrect);
            return;
        }

        ClientApi.inst().signUpByEmail(email, pwd, name, new ApiListener() {  //注册

            @Override
            public void onResponse(JSONObject response) {
                if(response == null)return;

                Log.i(TAG, "response = " + response.toString());
                ToastUtil.showToastLong(context,"onResponse = " + response.toString());

                int dm_error = response.optInt("dm_error", -1);//根据JSONO返回的关键字dm_error，给dm_error赋值
                if (dm_error == 0) {//ok
                    Intent toLoginActivity = new Intent();
                    toLoginActivity.setClass(context, LoginByEmailActivity.class);
                    startActivity(toLoginActivity);
                } else {
                    if(dm_error == 502){
                        ToastUtil.showToastLong(context,getString(R.string.email_exist));
                        Intent intent2 = new Intent();
                        intent2.setClass(context, LoginByEmailActivity.class);
                        startActivity(intent2);
                    }else{
                        if(dm_error == -1){
                            ToastUtil.showToastLong(context,getString(R.string.network_no_avaliable));
                        }else{
                            ToastUtil.showToastLong(context,getString(R.string.register_fail));
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

    public void flingUp() {
        AnimationUtils.hideToTop(editText_name, -1, null);
    }
}
