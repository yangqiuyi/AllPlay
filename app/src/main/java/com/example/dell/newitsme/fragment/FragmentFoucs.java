package com.example.dell.newitsme.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.example.controller.ActivityCBase;
import com.example.dell.newitsme.R;
import com.example.dell.newitsme.adapter.FoucsAdapter;
import com.example.model.ListViewAdapterModel;
import com.example.model.LiveItemModel;
import com.example.net.ApiListener;
import com.example.net.ClientApi;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class FragmentFoucs extends Fragment {
    public static  final String TAG = "FragmentFoucs";
    List<LiveItemModel> itemList = new ArrayList<>();
    List<ListViewAdapterModel> modelsList  = new ArrayList<>();

    public static final int TYPE_NO_DATA_LIVE = 0; //关注的好友没有直播列表数据
    public static final int TYPE_DATA_LIVE = 1; //关注的好友的直播列表数据
    public static final int TYPE_DATA_RECORD = 2; //回放列表数据

    private ListView mListView;
    private FoucsAdapter mFoucsAdapter;
    private ActivityCBase _activityCBase;

    public void init(ActivityCBase activityCBase){
        _activityCBase = activityCBase;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_fragment_foucs, container, false);

        mListView = (ListView)root.findViewById(R.id.listview_focus);
        mFoucsAdapter = new FoucsAdapter(_activityCBase);
        mListView.setAdapter(mFoucsAdapter);

         ClientApi.focusLivingList(new ApiListener() {
             @Override
             public void onResponse(JSONObject response) {
                 if(response == null)return;
                 Log.i(TAG,"response = "+response);
                 JSONArray foucs  = response.optJSONArray("lives");
                /*
lives: [
{
city: "",
group: 20001,
id: 7400616308743902,
creator: {
uid: 2070162,
nick: "{NCK}Modtanoy",
portrait: "9c235670e8b2937012e5190da518e443b12bc4f8_p_2070162.jpg",
gender: 0,
ulevel: 6,
location: "",
description: "IG : Anttiiz FB : VJ.Modtanoy"
},
image: "",
online_users: 92,
pub_stat: 1,
room_id: 87439,
share_addr: null,
status: "1",
stream_addr: null,
version: 0
}
],
dm_error: 0,
error_msg: "操作成功"*/
                if(foucs != null){
                    int size = foucs.length();
                    if(size > 0){//所关注的人有直播数据

                        for(int i = 0; i < foucs.length();i++){
                            JSONObject obj = foucs.optJSONObject(i);
                            if (obj == null) {
                                continue;
                            }

                            LiveItemModel live = LiveItemModel.fromJson(obj);
                            modelsList.add(new ListViewAdapterModel(TYPE_DATA_LIVE,live));
                        }

                        //
                        mFoucsAdapter.setData(modelsList);
                    }else {//没有数据
                        modelsList.add(new ListViewAdapterModel(TYPE_NO_DATA_LIVE,null));
                        mFoucsAdapter.setData(modelsList);
                    }
                }

                 //确保回看的数据在直播下面，方便布局UI
                 ClientApi.recorderList(0,new ApiListener(){
                     @Override
                     public void onResponse(JSONObject response) {
                         if (response == null) return;
                         JSONArray foucs_records  = response.optJSONArray("records");

                         if(foucs_records != null){
                             int size = foucs_records.length();
                             if(size > 0){//所关注的人有直播数据

                                 for(int i = 0; i < foucs_records.length();i++){
                                     JSONObject obj = foucs_records.optJSONObject(i);
                                     if (obj == null) {
                                         continue;
                                     }
                                     LiveItemModel record =  LiveItemModel.fromJson(obj);
                                     modelsList.add(new ListViewAdapterModel(TYPE_DATA_RECORD, record));

                                 }
                                 mFoucsAdapter.addData(modelsList);

                             }else {//没有数据

                             }
                         }
                     }

                     @Override
                     public void onErrorResponse(int statusCode, String exceptionMessage) {

                     }
                 });

             }

             @Override
             public void onErrorResponse(int statusCode, String exceptionMessage) {

             }
         });



        return root;
    }



}
