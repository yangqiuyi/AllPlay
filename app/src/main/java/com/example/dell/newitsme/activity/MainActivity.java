package com.example.dell.newitsme.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import com.example.dell.newitsme.R;
import com.example.dell.newitsme.activity.base.ActivityEventBase;
import com.example.dell.newitsme.fragment.FragmentHome;
import com.example.dell.newitsme.fragment.FragmentMe;
import com.example.util.Devices;

public class MainActivity extends ActivityEventBase implements View.OnClickListener{

    private FragmentHome _home;
    private FragmentMe _Me;
    private boolean isClicked = false;//按键防抖
    private  final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Devices.initDisplayMetrics(getWindowManager());
        //

        Button home = (Button)findViewById(R.id.bt_home);
        Button me = (Button)findViewById(R.id.bt_me);
        home.setOnClickListener(this);
        me.setOnClickListener(this);
        //
        initFragmentLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initFragmentLayout(){
        _home = new FragmentHome();
        _home.init(this);
        _Me = new FragmentMe();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, _home);
        ft.add(R.id.fragment_container, _Me);
        ft.hide(_Me);
        ft.commit();
    }

    private void onHome(){
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.hide(_Me);
        ft1.show(_home);
        ft1.commit();
    }

    private void onMe(){
        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
        ft2.hide(_home);
        ft2.show(_Me);
        ft2.commit();
    }

    @Override
    public void onClick(View view){
        if(isClicked)return;;

        isClicked = true;

        int id = view.getId();
        switch (id){
            case R.id.bt_home:
                    onHome();
                    isClicked = false;
                    break;
                case R.id.bt_me:
                    onMe();
                    isClicked = false;
                    break;
        }
    }

}
