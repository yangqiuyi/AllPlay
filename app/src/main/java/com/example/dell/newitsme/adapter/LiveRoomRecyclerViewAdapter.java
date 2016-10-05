package com.example.dell.newitsme.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.dell.newitsme.R;
import com.example.dell.newitsme.viewholder.LiveRoomRecyclerViewHolder;
import com.example.dell.newitsme.viewholder.RecyclerViewHolder;
import com.example.model.LiveItemModel;
import com.example.model.UserInfoModel;

import java.util.List;

public class LiveRoomRecyclerViewAdapter extends RecyclerView.Adapter{

    public LiveRoomRecyclerViewHolder mliveRoomRecyclerViewHolder;
    public static List<UserInfoModel> mDatas;

    public  void initData(List<UserInfoModel> list){
        mDatas = list;

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mliveRoomRecyclerViewHolder =
                new LiveRoomRecyclerViewHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.layout_liveroom_recyclerview_item,parent,false));//绑定每个item布局

        return mliveRoomRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mliveRoomRecyclerViewHolder, int position) {
        LiveRoomRecyclerViewHolder  liveRoomRecyclerViewHolder = (LiveRoomRecyclerViewHolder) mliveRoomRecyclerViewHolder;
        liveRoomRecyclerViewHolder.setData(mDatas.get(position));
    }
}
