package com.example.dell.newitsme.fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.dell.newitsme.R;
import com.example.dell.newitsme.adapter.RecyclerViewAdapter;
import com.example.dell.newitsme.viewholder.RecyclerViewHolder;
import com.example.model.LiveItemModel;
import com.example.net.ApiListener;
import com.example.net.ClientApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentNew extends Fragment {

    static final int DEFAULT_COUNT = 1000;
    private static final String TAG ="FragmentNew";
    public  List<LiveItemModel> itemList = new ArrayList<>();
    private RecyclerView mRecyclerView ;
    private RecyclerViewAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_fragment_new, container, false);

        mAdapter = new RecyclerViewAdapter();
        mRecyclerView = (RecyclerView)root.findViewById(R.id.id_recyclerview);
        mRecyclerView.setLayoutManager(new  GridLayoutManager(container.getContext(),3));
        mRecyclerView.addItemDecoration(new DividerItemLine(container.getContext(),DividerItemLine.VERTICAL_LIST));//垂直方向的线
        mRecyclerView.addItemDecoration(new DividerItemLine(container.getContext(),DividerItemLine.HORIZONTAL_LIST));//水平方向的线

        ClientApi.getNewList(DEFAULT_COUNT, new ApiListener() {
            @Override
            public void onResponse(JSONObject response) {      //   回调处  ，回调处在callCmd_POST（）
                if(response == null)return;
                Log.d(TAG, "response = " + response);
              //解析数据fullUrl = http://52.220.47.110/live/latest.php?count=1000
                /*服务器返回的jason
                {expire: 5,
                  lives: [],
                  dm_error: 0,
                  error_msg: "操作成功"
                  }*/
                JSONArray lives = response.optJSONArray("lives");//从json中选择lives字段  lives字段包含所需要解析的数据
                if(lives == null)return;

                int size = lives.length();
                itemList.clear();

                try {
                    for (int i = 0; i < size; i++){//遍历数组中的每一个元素item
                        JSONObject live = lives.getJSONObject(i);//数组
                        if(live == null)continue;

                        LiveItemModel itemModel =  LiveItemModel.fromJson(live);//调用LiveItemModel.fromJson(obj)去解析，数组lives中的每一个元素item，从而把名字，地址。。解出来
                        itemList.add(itemModel);//解到的每一个item数据放到集合里面
                    }
                    mAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    Log.e(TAG,"loadApiMap Exception:" + e.toString());
                }

                Log.d(TAG, "response = " + response);
            }

            @Override
            public void onErrorResponse(int statusCode, String exceptionMessage) {

            }
        });

        RecyclerViewAdapter.initData(itemList);
        mRecyclerView.setAdapter(mAdapter);
        return root;

    }
}