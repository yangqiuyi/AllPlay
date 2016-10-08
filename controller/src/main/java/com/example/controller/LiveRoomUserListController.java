package com.example.controller;


import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.model.LiveItemModel;
import com.example.model.UserInfoModel;
import com.example.net.ApiListener;
import com.example.net.ClientApi;
import com.example.net.ImageUrlParser;

import org.json.JSONArray;
import org.json.JSONObject;

public class LiveRoomUserListController extends ControllerBase{

    private static final String TAG = "LiveRoomUserListController";

    public LiveRoomUserListController(ActivityCBase activity) {
        super(activity);
    }


    //找控件
    public  void attach(LiveItemModel liveItemModel, View layout){
        if(liveItemModel == null || layout == null)return;

        layout.findViewById(R.i)


      /*  TextView textView = (TextView)layout.findViewById(R.id.live_room_name_name);
        TextView num = (TextView) layout.findViewById(R.id.live_room_people);
        SimpleDraweeView touxiang = (SimpleDraweeView)layout. findViewById(R.id.user_info_touxiang);

        String s = liveItemModel.getNick();
        int number = liveItemModel.getOnline_users();
        String portrait = liveItemModel.getPortrait();
        Log.i(TAG, s);

        textView.setText(s);
        num.setText(String.valueOf(number));
        touxiang.setImageURI(ImageUrlParser.avatarRoomHeadImageUrl(portrait));*/

    }

    public void requestHot(){

        ClientApi.roomUsers(liveItemModel.id, 0, 50, new ApiListener() {//异步
            @Override
            public void onResponse(JSONObject response) {
                if (response == null) {
                    return;
                }

                Log.i(TAG, "response" + response);
                JSONArray users = response.optJSONArray("users");//从json中选择lives字段  lives字段包含所需要解析的数据
                if (users == null) return;
                int size = users.length();

                try {
                    for (int i = 0; i < size; i++) {//遍历数组中的每一个元素item
                        JSONObject user = users.getJSONObject(i);//数组
                        if (user == null) continue;

                        UserInfoModel userInfoModel = new UserInfoModel();
                        userInfoModel.updateFromJson(user);
                        mItemList.add(userInfoModel);
                    }
                    mliveRoomRecyclerViewAdapter.notifyDataSetChanged();//数据改变,通知ui
                } catch (Exception e) {
                    Log.e(TAG, "loadApiMap Exception:" + e.toString());
                }


            }

            @Override
            public void onErrorResponse(int statusCode, String exceptionMessage) {

            }
        });
    }


    @Override
    protected void gc() {

    }

}
