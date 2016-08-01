package com.example.dell.newitsme.model;

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
 * */
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

        JSONObject creator = jsonObject.optJSONObject("creator");
        if (creator != null){
            liveItem.liveCreator.updateFromJson(creator);
        }
        return liveItem;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public void setName(String name) {
        this.name = name;

    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCity() {
        return city;
    }

    public String getImage() {
        return image;
    }

    public int getGroup() {
        return group;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UserInfoModel getLiveCreator() {
        return liveCreator;
    }
}
