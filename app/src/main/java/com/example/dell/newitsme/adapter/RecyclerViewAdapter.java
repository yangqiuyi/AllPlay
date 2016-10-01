package com.example.dell.newitsme.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.example.dell.newitsme.R;
import com.example.dell.newitsme.fragment.FragmentNew;
import com.example.dell.newitsme.viewholder.RecyclerViewHolder;
import com.example.model.LiveItemModel;

import java.util.ArrayList;
import java.util.List;

//数据与ui匹配
public class RecyclerViewAdapter extends RecyclerView.Adapter{

    public static List<LiveItemModel> mDatas;
    public  RecyclerViewHolder mRecyclerViewHolder;

    public  static void initData(List<LiveItemModel> list){
        mDatas = list;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       mRecyclerViewHolder =
               new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).
                       inflate(R.layout.layout_fragment_new_item,parent,false));//绑定每个item布局
        return mRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mRecyclerViewHolder, int position) {
        RecyclerViewHolder holder = (RecyclerViewHolder)mRecyclerViewHolder;//向下转型
       // holder.mTextView.setText(mDatas.get(position));
        holder.setData(mDatas.get(position));

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


}
