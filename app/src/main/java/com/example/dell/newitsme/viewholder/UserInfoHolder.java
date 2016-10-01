package com.example.dell.newitsme.viewholder;



import android.view.View;
import android.widget.TextView;

import com.example.dell.newitsme.R;
import com.example.model.UserInfoModel;
import com.example.net.ImageUrlParser;
import com.facebook.drawee.view.SimpleDraweeView;

public class UserInfoHolder {

    private TextView mNick;//UI
    private TextView mLevel;
    private TextView mID;
    private TextView mdescription;
    public SimpleDraweeView muser_touxiang;


    public void attach(View layout){//找UI
        if(layout == null)return;
        mNick = (TextView)layout.findViewById(R.id.user_nick);
        mLevel = (TextView)layout.findViewById(R.id.user_level);
        mID = (TextView)layout.findViewById(R.id.user_id);
        mdescription = (TextView)layout.findViewById(R.id.user_description);
        muser_touxiang = (SimpleDraweeView)layout.findViewById(R.id.user_info_touxiang);
    }

    public void setUser(UserInfoModel user){//匹配数据
        if(user == null)return;

        if(mNick != null){//判断UI是否为空
            String nick = user.nick;
            if(nick != null){//判断数据是否为空
                mNick.setText(nick);//UI和数据匹配
            }
        }
       if(mLevel != null){
            int level = user.ulevel;
            if(level != 0){
                mLevel.setText(String.valueOf(level));//UI和数据匹配
            }
        }
        if(mID != null){
           int id  = user.uid;
            if(id != 0){
                mID.setText(String.valueOf(id));
            }
        }
        if(mdescription != null){
            String description = user.description;
            if(description != null){
                mdescription.setText(description);
            }
        }
        if(muser_touxiang != null){
            String portrait = user.portrait;
            if(portrait != null){
                muser_touxiang.setImageURI(ImageUrlParser.avatarRoomHeadImageUrl(portrait));//把图片的链接加载进去
            }
        }

    }

}
