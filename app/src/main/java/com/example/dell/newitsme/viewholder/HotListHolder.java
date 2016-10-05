package com.example.dell.newitsme.viewholder;


import android.view.View;
import android.widget.TextView;
import com.example.dell.newitsme.R;
import com.example.event.TurtleEvent;
import com.example.event.TurtleEventType;
import com.example.model.LiveItemModel;
import com.example.net.ImageUrlParser;
import com.facebook.drawee.view.SimpleDraweeView;
import de.greenrobot.event.EventBus;

public class HotListHolder {

    public TextView mAddress;
    public TextView mName;
    public TextView mPeople;
    public SimpleDraweeView mPicture;
    public SimpleDraweeView mTouxiang;
    private static final String TAG = "HotListHolder";


    public void attach(View layout) {//找UI
        if(layout == null)return;
        mAddress = (TextView) layout.findViewById(R.id.user_address);
        mName = (TextView) layout.findViewById(R.id.user_name);
        mPeople = (TextView) layout.findViewById(R.id.guankan);
        mPicture = (SimpleDraweeView) layout.findViewById(R.id.user_picture);
        mTouxiang = (SimpleDraweeView) layout.findViewById(R.id.touxiang);
    }

    public void setData( LiveItemModel liveItem){

        if(liveItem == null)return;

        mName.setText(liveItem.liveCreator.nick);//数据和UI匹配
        //
        if (liveItem.city == null || "".equals(liveItem.city)){
            mAddress.setText("难道是火星？");
        }else{
            mAddress.setText(liveItem.city);
        }
        mPeople.setText(String.valueOf(liveItem.online_users));

        //portrait 链接
        String portrait =  liveItem.liveCreator.portrait;//每一个item在ArrayList中的位置的liveCreator(UserInfoModel)的图片的链接
        mPicture.setImageURI(ImageUrlParser.coverImageUrl(portrait));//获得方面图片链接的方法ImageUrlParser.coverImageUrl
        mTouxiang.setImageURI(ImageUrlParser.avatarRoomHeadImageUrl(portrait));//获得头像图片链接的方法

    }

     public void onclick(final LiveItemModel liveItemModel){
         mPicture.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View v) {
                 EventBus.getDefault().post(
                         new TurtleEvent(TurtleEventType.TYPE_USER_ON_CLICK_, liveItemModel));
             }

         });
     }
}