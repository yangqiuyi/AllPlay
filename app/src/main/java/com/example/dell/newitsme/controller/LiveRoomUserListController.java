package com.example.dell.newitsme.controller;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private LiveRoomRecyclerViewAdapter mAdapter;
    private LiveItemModel mLiveItemModel;
    private List<UserInfoModel> mItemList = new ArrayList<>();
    private  ActivityCBase mActivityCBase;
    private boolean mGc = false;


    public LiveRoomUserListController(ActivityCBase activity) {

        super(activity);
        mActivityCBase = activity;
        mGc = false;
    }

    private void init(){
        mAdapter = new LiveRoomRecyclerViewAdapter();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivityCBase);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//横向滚动的RecycleView
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter.initData(mItemList);//加载数据
        mRecyclerView.setAdapter(mAdapter);//设置适配器,数据和ui匹配
    }


    //找控件
    public  void attach(LiveItemModel liveItemModel, View layout){

        if(liveItemModel == null || layout == null)return;

        mLiveItemModel = liveItemModel;

        TextView textView = (TextView)layout.findViewById(R.id.live_room_name_name);
        TextView num = (TextView) layout.findViewById(R.id.live_room_people);
        mRecyclerView = (RecyclerView)layout.findViewById(R.id.room_recyclerView);
        SimpleDraweeView touxiang = (SimpleDraweeView)layout. findViewById(R.id.user_info_touxiang);

        String s = mLiveItemModel.getNick();
        int number = mLiveItemModel.getOnline_users();
        String portrait = mLiveItemModel.getPortrait();

        textView.setText(s);
        num.setText(String.valueOf(number));
        touxiang.setImageURI(ImageUrlParser.avatarRoomHeadImageUrl(portrait));

        init();

    }

    public void requestHot() {

        ClientApi.roomUsers(mLiveItemModel.id, 0, 50, new ApiListener() {//异步
            @Override
            public void onResponse(JSONObject response) {
                if (response == null) {
                    return;
                }

                if(mGc){
                    return;
                }


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

                    mAdapter.notifyDataSetChanged();//数据改变,通知ui
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onErrorResponse(int statusCode, String exceptionMessage) {

            }
        });



    }


    @Override
    protected void gc() {
        if(mItemList != null){
            mItemList.clear();
            mItemList = null;
        }

        mGc = true;

        mLiveItemModel = null;
        mAdapter = null;
        mAdapter = null;
    }

}
