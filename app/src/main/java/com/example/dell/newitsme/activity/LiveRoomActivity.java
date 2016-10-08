package com.example.dell.newitsme.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.TextView;

import com.example.dell.newitsme.R;
import com.example.dell.newitsme.adapter.LiveRoomRecyclerViewAdapter;
import com.example.dell.newitsme.controller.ActivityCBase;
import com.example.dell.newitsme.controller.LiveRoomUserListController;
import com.example.dell.newitsme.controller.LiveRoomVideoController;
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


    private LiveRoomUserListController mUserListController;
    private LiveRoomVideoController mVideoController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_live_room);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //观看模式 - 房主信息
        LiveItemModel liveItemModel = bundle.getParcelable(LiveItemModel.TAG);

        //user list
        mUserListController = new LiveRoomUserListController(this);
        //找控件
        mUserListController.attach(liveItemModel,findViewById(R.id.root_user_list));
        //解析数据
        mUserListController.requestHot();

        //video play
        String streamName = "" + liveItemModel.liveCreator.uid;
        String sessionId = liveItemModel.id;
        mVideoController = new LiveRoomVideoController(this);
        mVideoController.init(streamName,sessionId, (SurfaceView)findViewById(R.id.surface_view));
    }

}




