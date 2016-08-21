package com.example.dell.newitsme.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.SelfInfo;
import com.example.dell.newitsme.R;
import com.example.dell.newitsme.fragment.base.FragmentBase;
import com.example.dell.newitsme.viewholder.UserInfoHolder;
import com.example.event.TurtleEvent;
import com.example.event.TurtleEventType;

public class FragmentMe extends FragmentBase {

    public  UserInfoHolder userInfoHolder = new UserInfoHolder();
    public static final String TAG = "FragmentMe";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_fragment_me, container, false);
        userInfoHolder.attach(root);
        userInfoHolder.setUser(SelfInfo.inst()._userInfo);
        super.registerEventBus();
        return root;
    }

    public  void onEventMainThread(TurtleEvent event){
        int type = event.getType();
        if (type == TurtleEventType.TYPE_USER_INFO_UPDATE) {
            userInfoHolder.setUser(SelfInfo.inst()._userInfo);
            Log.i(TAG,SelfInfo.inst()._userInfo.nick);
            Log.i(TAG,SelfInfo.inst()._userInfo.description);
        }
    }

}
