package com.example.dell.newitsme.controller;

import android.util.Log;

import com.example.event.TurtleEvent;
import com.example.event.TurtleEventType;
import com.example.model.LiveItemModel;
import com.example.net.ApiListener;
import com.example.net.ClientApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
   *
   *
   * 解析数据
 *
 *
 * */

public class HotDataController extends ControllerBase {

    private final String TAG = "HotDataController";

    public HotDataController(ActivityCBase activity) {
        super(activity);
    }

    public void requestHot() {
        ClientApi.inst().hotList(new ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                if (response == null) return;
                Log.i(TAG, "response = " + response.toString());
                EventBus.getDefault().post(new TurtleEvent(TurtleEventType.TYPE_HOT_DATA_OK, paraseLiveItemData(response)));//发送消息事件
            }

            @Override
            public void onErrorResponse(int statusCode, String exceptionMessage) {
                Log.i(TAG, "exceptionMessage = " + exceptionMessage);
                //ui 点击重试
            }
        });
    }

    public List<LiveItemModel> paraseLiveItemData(JSONObject response) {
        if(response == null) return null;

        List<LiveItemModel> itemList = new ArrayList<>();
        JSONArray jsonArray = response.optJSONArray("lives");//选中返回字段中的lives lives 是一个数组
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.optJSONObject(i);//返回一个JSONObject对象 ---》obj
                if (obj == null) {
                    break;
                }
                LiveItemModel live = LiveItemModel.fromJson(obj);//调用LiveItemModel.fromJson(obj)去解析，数组lives中的每一个元素item，从而把名字，地址。。解出来
                itemList.add(live);
            }
        }
        return itemList;
    }

    @Override
    protected void gc() {
        Log.i(TAG, "gc");
    }

}
