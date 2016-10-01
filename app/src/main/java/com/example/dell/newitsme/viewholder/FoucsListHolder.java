package com.example.dell.newitsme.viewholder;


import android.view.View;
import android.widget.TextView;
import com.example.dell.newitsme.R;
import com.example.model.LiveItemModel;
import com.example.net.ImageUrlParser;
import com.facebook.drawee.view.SimpleDraweeView;

public class FoucsListHolder {

    public TextView mNick;
    public TextView mLevel;
    public SimpleDraweeView mPicture;
    public TextView mDescription;
    public TextView mClick_users;//观看人数

    public void attach(View layout) {//找UI

        mNick =(TextView) layout.findViewById(R.id.user_name);
        mLevel = (TextView) layout.findViewById(R.id.user_level);
        mPicture = (SimpleDraweeView)layout.findViewById(R.id.touxiang);
        mDescription = (TextView) layout.findViewById(R.id.user_description);
        mClick_users = (TextView) layout.findViewById(R.id.guankan);

        }

    public void setData( LiveItemModel foucsItem) {

        if (foucsItem == null) return;
        mNick.setText(foucsItem.liveCreator.nick);
        mLevel.setText(String.valueOf(foucsItem.liveCreator.ulevel));
        mDescription.setText(foucsItem.liveCreator.description);
       mClick_users.setText(String.valueOf(foucsItem.click_users));

        String portrait =  foucsItem.liveCreator.portrait;
       mPicture.setImageURI(ImageUrlParser.avatarRoomHeadImageUrl(portrait));
    }

}
