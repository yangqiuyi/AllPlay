package com.example.dell.newitsme.controller;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.dell.newitsme.R;
import com.example.dell.newitsme.adapter.LiveRoomRecyclerViewAdapter;
import com.example.model.LiveItemModel;
import com.example.model.UserInfoModel;
import com.example.net.ApiListener;
import com.example.net.ClientApi;
import com.example.net.ImageUrlParser;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LiveRoomUserListController extends ControllerBase {

    private static final String TAG = "LiveRoomUserListController";
    private RecyclerView mRecyclerView;
    private LiveRoomRecyclerViewAdapter mliveRoomRecyclerViewAdapter;
    public List<UserInfoModel> mItemList = new ArrayList<>();
   public  ActivityCBase mActivityCBase;


    public LiveRoomUserListController(ActivityCBase activity) {

        super(activity);
        mActivityCBase = activity;

    }


    //找控件
    public  void attach(LiveItemModel liveItemModel, View layout){

        if(liveItemModel == null || layout == null)return;

        TextView textView = (TextView)layout.findViewById(R.id.live_room_name_name);
        TextView num = (TextView) layout.findViewById(R.id.live_room_people);
        mRecyclerView = (RecyclerView)layout.findViewById(R.id.room_recyclerView);
        SimpleDraweeView touxiang = (SimpleDraweeView)layout. findViewById(R.id.user_info_touxiang);

        String s = liveItemModel.getNick();
        int number = liveItemModel.getOnline_users();
        String portrait = liveItemModel.getPortrait();


        textView.setText(s);
        num.setText(String.valueOf(number));
        touxiang.setImageURI(ImageUrlParser.avatarRoomHeadImageUrl(portrait));


    }

    public void requestHot(LiveItemModel liveItemModel , final List<UserInfoModel> itemList)
    {

        ClientApi.roomUsers(liveItemModel.id, 0, 50, new ApiListener() {//异步
            @Override
            public void onResponse(JSONObject response) {
                if (response == null) {
                    return;
                }

          /*      Log.d(TAG, "response" + response);*/
                JSONArray users = response.optJSONArray("users");//从json中选择lives字段  lives字段包含所需要解析的数据
                if (users == null) return;
                int size = users.length();

                try {
                    for (int i = 0; i < size; i++) {//遍历数组中的每一个元素item
                        JSONObject user = users.getJSONObject(i);//数组
                        if (user == null) continue;

                        UserInfoModel userInfoModel = new UserInfoModel();
                        userInfoModel.updateFromJson(user);
                        itemList.add(userInfoModel);
                        int sizes = itemList.size();

                    }

                    mliveRoomRecyclerViewAdapter.notifyDataSetChanged();//数据改变,通知ui
                } catch (Exception e) {
                   // Log.e(TAG, "loadApiMap Exception:" + e.toString());
                }


            }

            @Override
            public void onErrorResponse(int statusCode, String exceptionMessage) {

            }
        });

        mItemList = itemList;
        mliveRoomRecyclerViewAdapter = new LiveRoomRecyclerViewAdapter();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivityCBase);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//横向滚动的RecycleView
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mliveRoomRecyclerViewAdapter.initData(mItemList);//加载数据
        mRecyclerView.setAdapter(mliveRoomRecyclerViewAdapter);//设置适配器,数据和ui匹配
    }


    @Override
    protected void gc() {

    }

}
