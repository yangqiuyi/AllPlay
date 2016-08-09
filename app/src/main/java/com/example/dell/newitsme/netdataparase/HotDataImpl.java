package com.example.dell.newitsme.netdataparase;

import android.util.Log;

import com.example.dell.newitsme.adapter.LineHotListAdapter;
import com.example.dell.newitsme.event.TurtleEvent;
import com.example.dell.newitsme.event.TurtleEventType;
import com.example.dell.newitsme.model.LiveItemModel;
import com.example.dell.newitsme.net.ApiListener;
import com.example.dell.newitsme.net.ClientApi;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

//数据请求， 解析数据
public class HotDataImpl implements IHotData {

    private static final String TAG = "HotDataImpl";
    private LineHotListAdapter mAdapter;

    public HotDataImpl(LineHotListAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public void requestHot() {
        ClientApi.inst().hotList(new ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                if (response == null) return;
                Log.i(TAG, "response = " + response.toString());
                mAdapter.setData(paraseLiveItemData(response));//把获得的数据传给Adapter
                EventBus.getDefault().post(new TurtleEvent(TurtleEventType.TYPE_HOT_DATA_OK));//发送消息事件
            }

            @Override
            public void onErrorResponse(int statusCode, String exceptionMessage) {
                Log.i(TAG, "exceptionMessage = " + exceptionMessage);
                //ui 点击重试
            }
        });
    }

    public List<LiveItemModel> paraseLiveItemData(JSONObject response) {
        List<LiveItemModel> itemList = new ArrayList<>();
        JSONArray jsonArray = response.optJSONArray("lives");
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.optJSONObject(i);//返回一个JSONObject对象 ---》obj
                if (obj == null) {
                    break;
                }
                LiveItemModel live = LiveItemModel.fromJson(obj);
                itemList.add(live);
            }
        }
        return itemList;
    }


}