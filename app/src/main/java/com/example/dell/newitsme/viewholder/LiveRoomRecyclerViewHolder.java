package com.example.dell.newitsme.viewholder;


import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.dell.newitsme.R;
import com.example.model.LiveItemModel;
import com.example.model.UserInfoModel;
import com.example.net.ImageUrlParser;
import com.facebook.drawee.view.SimpleDraweeView;

public class LiveRoomRecyclerViewHolder extends RecyclerView.ViewHolder{

    public SimpleDraweeView mNewsPicture;

    public LiveRoomRecyclerViewHolder(View layout) {
        super(layout);
        mNewsPicture = (SimpleDraweeView)layout.findViewById(R.id.user_info_touxiang);
    }

    public  void setData( UserInfoModel itemModel){
        if (itemModel == null)return;;

        //portrait 链接
        String portrait =  itemModel.portrait;//每一个item在ArrayList中的位置的liveCreator(UserInfoModel)的图片的链接
        mNewsPicture.setImageURI(ImageUrlParser.avatarRoomHeadImageUrl(portrait));
    }



}
