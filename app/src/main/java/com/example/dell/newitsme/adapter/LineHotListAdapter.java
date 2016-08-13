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
import com.example.model.LiveItemModel;
import com.example.net.ImageUrlParser;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.ArrayList;
import java.util.List;

//数据和UI结合
public class LineHotListAdapter extends BaseAdapter{

        private  LayoutInflater inflater;//LayoutInflater是用来找res/layout/下的xml布局文件，并且实例化
        private  List<LiveItemModel>  mDatas = new ArrayList<>();
        private HotDataController _hotDataController;
        private static final String TAG = "LineHotListAdapter";

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
                viewHolder.touxiang = (SimpleDraweeView) view.findViewById(R.id.touxiang);
                view.setTag(viewHolder);
            }

            ViewHolder viewHolder = (ViewHolder) view.getTag();
            LiveItemModel  liveItem =  mDatas.get(position);
            //
            if (liveItem.city == null || "".equals(liveItem.city)){
                viewHolder.address.setText("难道是火星？");
            }else{
                viewHolder.address.setText(liveItem.city);
            }
            //
            viewHolder.name.setText(liveItem.liveCreator.nick);
            viewHolder.people.setText(String.valueOf(liveItem.online_users));
            //portrait 链接
            String portrait =  liveItem.liveCreator.portrait;//每一个item在ArrayList中的位置的liveCreator(UserInfoModel)的图片的链接
            viewHolder.picture.setImageURI(ImageUrlParser.coverImageUrl(portrait));//获得方面图片链接的方法ImageUrlParser.coverImageUrl
            viewHolder.touxiang.setImageURI(ImageUrlParser.avatarRoomHeadImageUrl(portrait));//获得头像图片链接的方法
            return view;
        }

      private class ViewHolder {
          public TextView address;
          public TextView name;
          public TextView people;
          public SimpleDraweeView picture;
          public SimpleDraweeView touxiang;
      }

    }