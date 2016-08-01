package com.example.dell.newitsme.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.dell.newitsme.R;
import com.example.dell.newitsme.model.LiveItemModel;
import com.example.dell.newitsme.netdata.HotDataImpl;
import com.example.dell.newitsme.netdata.IHotData;
import com.example.dell.newitsme.util.ImageUrlParser;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.ArrayList;
import java.util.List;

//数据和UI结合
public class LineHotListAdapter extends BaseAdapter implements IHotData{
        private  LayoutInflater inflater;//LayoutInflater是用来找res/layout/下的xml布局文件，并且实例化
        private  List<LiveItemModel>  mDatas = new ArrayList<>();
        private  IHotData mIHotData;//接口
        private static final String TAG = "LineHotListAdapter";

        public LineHotListAdapter(Context context){
            inflater = LayoutInflater.from(context);
            mIHotData = new HotDataImpl(this);
        }

        @Override
        public void requestHot() {//这个方法只是为了提供一个方法给适配器引用IHotData的requestHot()去请求数据
             mIHotData.requestHot();//调用，线上请求数据
        }

        public void setData(List<LiveItemModel> data){//被HotDataImpl的requestHot()调用，在requestHot()获得数据后，传给适配器
           if(data == null)return;

           mDatas = data;
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

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.live_item_big, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.address = (TextView) view.findViewById(R.id.user_address);//相当于布局live_item_big中地址为user_address的textview为address，以后便可通过adress操作textview
                viewHolder.name = (TextView) view.findViewById(R.id.user_name);
                viewHolder.people = (TextView) view.findViewById(R.id.guankan);
                viewHolder.picture = (SimpleDraweeView) view.findViewById(R.id.user_picture);
                view.setTag(viewHolder);
            }

            ViewHolder viewHolder = (ViewHolder) view.getTag();
            viewHolder.address.setText(mDatas.get(position).city);
            viewHolder.name.setText(mDatas.get(position).name);
            viewHolder.people.setText(String.valueOf(mDatas.get(position).online_users));
            viewHolder.picture.setImageURI(ImageUrlParser.coverImageUrl( mDatas.get(position).liveCreator.portrait));
            return view;
        }

      private class ViewHolder {
          public TextView address;
          public TextView name;
          public TextView people;
          public SimpleDraweeView picture;
      }

    }