package com.example.dell.newitsme.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.example.dell.newitsme.R;
import com.example.model.LiveItemModel;
import com.example.net.ImageUrlParser;
import com.facebook.drawee.view.SimpleDraweeView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder{
  //  public TextView mTextView;
    public SimpleDraweeView mNewsPicture;

    public RecyclerViewHolder(View layout) {
        super(layout);
      //  mTextView = (TextView)layout.findViewById(R.id.tv_recyclerview_item);
        mNewsPicture = (SimpleDraweeView)layout.findViewById(R.id.news_pictrue);
    }

    public  void setData( LiveItemModel itemModel){
        if (itemModel == null)return;;

        //portrait 链接
        String portrait =  itemModel.liveCreator.portrait;//每一个item在ArrayList中的位置的liveCreator(UserInfoModel)的图片的链接
        mNewsPicture.setImageURI(ImageUrlParser.coverImageUrl(portrait));
    }

}
