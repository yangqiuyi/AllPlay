package com.example.dell.newitsme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.example.dell.newitsme.R;
import com.example.dell.newitsme.controller.ActivityCBase;
import com.example.dell.newitsme.viewholder.FoucsListHolder;
import com.example.dell.newitsme.viewholder.HotListHolder;
import com.example.model.ListViewAdapterModel;
import com.example.model.LiveItemModel;
import java.util.ArrayList;
import java.util.List;

public class FoucsAdapter extends BaseAdapter {

    public static final int TYPE_NO_DATA_LIVE = 0; //关注的好友没有直播列表数据
    public static final int TYPE_DATA_LIVE = 1; //关注的好友的直播列表数据
    public static final int TYPE_DATA_RECORD = 2; //回放列表数据

    private LayoutInflater inflater ;//LayoutInflater是用来找res/layout/下的xml布局文件，并且实例化
    private List<ListViewAdapterModel> mDatas = new ArrayList<>();

    public FoucsAdapter(ActivityCBase activityCBase){
        inflater =  LayoutInflater.from(activityCBase);
    }

    public void setData(List<ListViewAdapterModel> data){
        mDatas = data;
        notifyDataSetChanged();
    }

    public void addData(List<ListViewAdapterModel> data){
        mDatas.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return mDatas.get(position).type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        int type = getItemViewType(position);


            if(type == TYPE_DATA_LIVE){
                view = inflater.inflate(R.layout.live_item_big, parent, false);
                HotListHolder hotListHolder = new HotListHolder();
                hotListHolder.attach(view);
                ListViewAdapterModel  liveItem =  mDatas.get(position);
                hotListHolder.setData((LiveItemModel)liveItem.obj);
            }
           else if(type == TYPE_NO_DATA_LIVE){
                view = inflater.inflate(R.layout.live_item_no_live, parent, false);
            }

            else if(type == TYPE_DATA_RECORD){
                view = inflater.inflate(R.layout.layout_fragment_foucs_item, parent, false);
                FoucsListHolder foucsListHolder = new FoucsListHolder();
                foucsListHolder.attach(view);
                ListViewAdapterModel  liveItem =  mDatas.get(position);
                foucsListHolder.setData((LiveItemModel)liveItem.obj);
            }


        return  view;
    }


}
