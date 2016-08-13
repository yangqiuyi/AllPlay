package com.example.model;

import android.util.Log;

import org.json.JSONObject;

/***
 *   lives: [
     {

     city: "",
     country: "CN",
     group: 20003,
     id: "6763243717326502",

         creator: {
         uid: 2241746,
         nick: "Anna Garrison",
         portrait: "9c1564d2ed6906028e1b248809b19dcf6852d3d8_p_2241746.jpg",
         gender: 0
       },

     image: "",
     online_users: 8,
     pub_stat: 1,
     room_id: 173265,
     share_addr: null,
     status: "1",
     stream_addr: null,
     version: 0,
     name: "Manicure, pedicure ",
     score: "0",
     real_score: 20000008
     },
 *
 * */ //房间模型，
public class LiveItemModel {
    public static final String TAG = "LiveItemModel";

    public String  city;
    public String id;
    public int group;
    public String  image;
    public int online_users;
    public int pub_stat;
    public int room_id;
    public String  share_addr;
    public String  status;
    public String  stream_addr;
    public String  name;
    public int version;
    public String  score;
    public int real_score;
    public UserInfoModel liveCreator = new UserInfoModel();


    public static LiveItemModel fromJson(JSONObject jsonObject){
        LiveItemModel liveItem = new LiveItemModel();
        liveItem.id = jsonObject.optString("id");

        if (liveItem.id == null || liveItem.id == ""){
            Log.i(TAG,"null or empty id!");
        }
        liveItem.group = jsonObject.optInt("group");
        liveItem.online_users = jsonObject.optInt("online_users");
        liveItem.city = jsonObject.optString("city");
        liveItem.name = jsonObject.optString("name");
        liveItem.image = jsonObject.optString("image");
        liveItem.share_addr = jsonObject.optString("share_addr");

        JSONObject creator = jsonObject.optJSONObject("creator");
        if (creator != null){
            liveItem.liveCreator.updateFromJson(creator);
        }
        return liveItem;
    }


}
