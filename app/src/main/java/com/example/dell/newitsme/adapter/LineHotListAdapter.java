package com.example.dell.newitsme.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.controller.ActivityCBase;
import com.example.controller.HotDataController;
import com.example.dell.newitsme.R;
import com.example.dell.newitsme.viewholder.HotListHolder;
import com.example.model.LiveItemModel;
import java.util.ArrayList;
import java.util.List;

//数据和UI结合
public class LineHotListAdapter extends BaseAdapter{

        private  LayoutInflater inflater;//LayoutInflater是用来找res/layout/下的xml布局文件，并且实例化
        private  List<LiveItemModel>  mDatas = new ArrayList<>();
        private HotDataController _hotDataController;
        private static final String TAG = "LineHotListAdapter";

       // private
       // on

        public LineHotListAdapter(ActivityCBase activityCBase){
            inflater = LayoutInflater.from(activityCBase);
            _hotDataController = new HotDataController(activityCBase);
        }

        public void requestHot() {
            _hotDataController.requestHot();
        }

        public void setData(List<LiveItemModel> liveItem){
           if(liveItem == null)return;

           mDatas = liveItem;
           notifyDataSetChanged();
        }

       @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            Log.i(TAG,"位置："+position);
            return mDatas.get(position);
        }


    /*private boolean mRequest = true;
    public void setResquet(boolean resquet){
        mRequest = resquet;
    }
*/
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.live_item_big, parent, false);
                HotListHolder hotListHolder = new HotListHolder();
                hotListHolder.attach(view);
                view.setTag(hotListHolder);//设置标签 的数据为hotListHolder
            }
            HotListHolder hotListHolder = (HotListHolder) view.getTag();//缓存机制（简单的引用），取出标签的数据从而达到复用
            LiveItemModel  liveItem =  mDatas.get(position);
          /*  hotListHolder.setResquet(mRequest);//todo*/
            hotListHolder.setData(liveItem);
            return view;
        }
    }