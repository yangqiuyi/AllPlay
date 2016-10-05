package com.example.dell.newitsme.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.dell.newitsme.R;
import com.example.dell.newitsme.adapter.LiveRoomRecyclerViewAdapter;
import com.example.dell.newitsme.controller.ActivityCBase;
import com.example.dell.newitsme.controller.LiveRoomUserListController;
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

public class LiveRoomActivity extends ActivityCBase {

    private static final String TAG = "LiveRoomActivity";
    public List<UserInfoModel> mItemList = new ArrayList<>();
    private LiveRoomUserListController mUserListController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_live_room);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();
        LiveItemModel liveItemModel = bundle.getParcelable(LiveItemModel.TAG);

        mUserListController = new LiveRoomUserListController(this);
        //找控件
        mUserListController.attach(liveItemModel,findViewById(R.id.root_user_list));
        //解析数据
        mUserListController.requestHot(liveItemModel,mItemList);





        }
    }




